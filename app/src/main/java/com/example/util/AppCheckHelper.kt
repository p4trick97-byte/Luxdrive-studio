package com.example.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume

private const val TAG = "AppCheckInit"

// App Check contract keys. These strings are defined by the Firebase SDK and
// the runtime harness, so do not rename them.
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

sealed interface AppCheckTokenState {
    data object NotChecked : AppCheckTokenState
    data object Checking : AppCheckTokenState
    data class TokenValid(val expirationHint: String = "Attivo") : AppCheckTokenState
    data class TokenRejectedByServer(val reason: String, val debugSecret: String?) : AppCheckTokenState
    data class TokenError(val error: String) : AppCheckTokenState
}

data class AppCheckDiagnostics(
    val isFirebaseConfigured: Boolean,
    val firebaseProjectId: String,
    val isDebugMode: Boolean,
    val activeProviderName: String,
    val debugSecret: String?,
    val tokenState: AppCheckTokenState
)

object AppCheckHelper {

    @Volatile
    private var isProviderInstalled = false

    fun isFirebaseConfigured(context: Context): Boolean {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            Log.d(TAG, "Firebase non inizializzato: ${e.message}")
            false
        }
    }

    fun getPersistenceKey(): String {
        return try {
            FirebaseApp.getInstance().persistenceKey
        } catch (e: Exception) {
            "[DEFAULT]"
        }
    }

    /**
     * Restituisce o genera in modo stabile il Debug Secret memorizzato nelle SharedPreferences
     * private del dispositivo, nella chiave attesa da DebugAppCheckProvider.
     * Questo permette a chi testa direttamente da smartphone di visualizzare e copiare
     * il token senza dover usare un PC o ADB Logcat.
     */
    fun getOrCreateDebugSecret(context: Context): String {
        val persistenceKey = getPersistenceKey()
        val prefs = context.getSharedPreferences(
            "$DEBUG_STORE_PREFIX$persistenceKey",
            Context.MODE_PRIVATE
        )
        var secret = prefs.getString(DEBUG_SECRET_KEY, null)
        if (secret.isNullOrBlank()) {
            secret = UUID.randomUUID().toString()
            prefs.edit().putString(DEBUG_SECRET_KEY, secret).commit()
            Log.d(TAG, "Nuovo debug secret generato e salvato localmente.")
        }
        return secret
    }

    /**
     * Consente di impostare un token di debug esplicito inserito manualmente dall'utente
     * (ad esempio generato preventivamente nella Firebase Console da smartphone).
     */
    fun setCustomDebugSecret(context: Context, token: String): Boolean {
        if (token.isBlank()) return false
        val persistenceKey = getPersistenceKey()
        val prefs = context.getSharedPreferences(
            "$DEBUG_STORE_PREFIX$persistenceKey",
            Context.MODE_PRIVATE
        )
        return prefs.edit().putString(DEBUG_SECRET_KEY, token.trim()).commit()
    }

    fun configureAppCheck(context: Context, intent: Intent?) {
        if (isProviderInstalled) {
            Log.d(TAG, "Provider App Check già installato. Salto configurazione ridondante.")
            return
        }

        try {
            if (!isFirebaseConfigured(context)) {
                Log.i(TAG, "Firebase non è configurato. App Check verrà configurato dopo il provisioning.")
                return
            }

            if (BuildConfig.DEBUG) {
                // 1. Controlla se l'intent fornisce un token debug esplicito
                val intentToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
                if (!intentToken.isNullOrEmpty()) {
                    setCustomDebugSecret(context, intentToken)
                    Log.d(TAG, "Debug secret configurato dall'Intent extra.")
                } else {
                    // 2. Assicura che esista un secret stabile salvato prima di installare il provider
                    getOrCreateDebugSecret(context)
                }

                // 3. Installa DebugAppCheckProviderFactory prima di qualsiasi richiesta Firebase
                FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
                isProviderInstalled = true
                Log.d(TAG, "DebugAppCheckProviderFactory installato con successo.")
            } else {
                // Release: Utilizza Play Integrity come strategia primaria per Android nativo
                try {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        PlayIntegrityAppCheckProviderFactory.getInstance()
                    )
                    isProviderInstalled = true
                    Log.d(TAG, "PlayIntegrityAppCheckProviderFactory installato per la build Release.")
                } catch (e: Exception) {
                    Log.w(TAG, "PlayIntegrity non avviato, verifica fallback reCAPTCHA: ${e.message}")
                    val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
                    if (resId != 0) {
                        val siteKey = context.getString(resId)
                        if (siteKey.isNotBlank()) {
                            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                                RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                            )
                            isProviderInstalled = true
                            Log.d(TAG, "RecaptchaAppCheckProviderFactory installato come fallback.")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore durante l'inizializzazione di Firebase App Check", e)
        }
    }

    /**
     * Esegue una verifica reale dell'attestazione App Check interrogando il backend Firebase.
     * Distingue con precisione:
     * 1. Firebase non inizializzato
     * 2. Token rifiutato dal server (403 / "token is invalid" -> debug secret non registrato in console)
     * 3. Errori di connettività
     * 4. Token valido accettato dal server
     */
    suspend fun verifyAppCheckToken(
        context: Context,
        forceRefresh: Boolean = false
    ): AppCheckDiagnostics {
        val configured = isFirebaseConfigured(context)
        val projectId = try {
            FirebaseApp.getInstance().options.projectId.orEmpty()
        } catch (e: Exception) {
            "Non disponibile"
        }
        val isDebug = BuildConfig.DEBUG
        val providerName = if (isDebug) "DebugAppCheckProviderFactory" else "PlayIntegrityAppCheckProviderFactory"
        val debugSecret = if (isDebug) getOrCreateDebugSecret(context) else null

        if (!configured) {
            return AppCheckDiagnostics(
                isFirebaseConfigured = false,
                firebaseProjectId = projectId,
                isDebugMode = isDebug,
                activeProviderName = providerName,
                debugSecret = debugSecret,
                tokenState = AppCheckTokenState.TokenError("Firebase non è inizializzato (file google-services.json non rilevato o non valido).")
            )
        }

        val tokenResult = suspendCancellableCoroutine<Result<String>> { cont ->
            try {
                FirebaseAppCheck.getInstance().getAppCheckToken(forceRefresh)
                    .addOnSuccessListener { appCheckToken ->
                        if (appCheckToken != null && appCheckToken.token.isNotBlank()) {
                            cont.resume(Result.success(appCheckToken.token))
                        } else {
                            cont.resume(Result.failure(IllegalStateException("Il server App Check ha restituito un token vuoto.")))
                        }
                    }
                    .addOnFailureListener { exception ->
                        cont.resume(Result.failure(exception))
                    }
            } catch (e: Exception) {
                cont.resume(Result.failure(e))
            }
        }

        val tokenState = tokenResult.fold(
            onSuccess = { token ->
                AppCheckTokenState.TokenValid("Token verificato (${token.take(8)}...)")
            },
            onFailure = { error ->
                val msg = error.message.orEmpty()
                val isRejected = msg.contains("403") ||
                    msg.contains("invalid", ignoreCase = true) ||
                    msg.contains("PERMISSION_DENIED", ignoreCase = true) ||
                    msg.contains("app check", ignoreCase = true)

                if (isRejected && isDebug) {
                    AppCheckTokenState.TokenRejectedByServer(
                        reason = "Il server Firebase ha rifiutato l'attestazione. Il Debug Secret di questo Galaxy S23 non è ancora registrato nella Firebase Console del progetto.",
                        debugSecret = debugSecret
                    )
                } else if (msg.contains("host", ignoreCase = true) || msg.contains("ConnectException", ignoreCase = true)) {
                    AppCheckTokenState.TokenError("Connessione di rete assente o non raggiungibile.")
                } else {
                    AppCheckTokenState.TokenError(error.localizedMessage ?: "Errore attestazione App Check.")
                }
            }
        )

        return AppCheckDiagnostics(
            isFirebaseConfigured = true,
            firebaseProjectId = projectId,
            isDebugMode = isDebug,
            activeProviderName = providerName,
            debugSecret = debugSecret,
            tokenState = tokenState
        )
    }
}
