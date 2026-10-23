package com.example.data.service

import android.content.Context
import android.util.Log
import com.example.util.AppCheckHelper
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "GeminiAutomotiveAi"
private const val GENERATION_TIMEOUT_MS = 120_000L // 120 secondi per consentire il completamento di script completi

class AiNotConfiguredException(message: String) : IllegalStateException(message)

sealed interface AiBackendStatus {
    data object FirebaseAiLogicActive : AiBackendStatus
    data class NotConfigured(val explanation: String) : AiBackendStatus
}

class GeminiAutomotiveAiClient(private val context: Context) {

    fun getBackendStatus(): AiBackendStatus {
        if (AppCheckHelper.isFirebaseConfigured(context)) {
            return AiBackendStatus.FirebaseAiLogicActive
        }

        return AiBackendStatus.NotConfigured(
            explanation = "Firebase AI Logic non ancora configurato nel modulo app.\n\n" +
                "Per attivare la generazione degli script con Gemini Developer API:\n" +
                "1. Assicurati che il file 'google-services.json' sia presente nella cartella 'app/' del repository.\n" +
                "2. Verifica che il package name nel file corrisponda all'applicationId dell'app: 'com.aistudio.luxdrive.zqkpmx'.\n" +
                "3. Verifica che Firebase AI Logic (Gemini Developer API) sia abilitato nella Firebase Console del progetto.\n\n" +
                "In assenza della configurazione Firebase, non verranno generati contenuti fittizi."
        )
    }

    suspend fun generateScript(
        request: AiScriptRequest,
        onProgress: ((String) -> Unit)? = null
    ): Result<GeneratedAutomotiveScript> = withContext(Dispatchers.IO) {
        val status = getBackendStatus()

        when (status) {
            is AiBackendStatus.FirebaseAiLogicActive -> {
                generateViaFirebaseAiLogic(request, onProgress)
            }
            is AiBackendStatus.NotConfigured -> {
                Result.failure(AiNotConfiguredException(status.explanation))
            }
        }
    }

    private suspend fun generateViaFirebaseAiLogic(
        request: AiScriptRequest,
        onProgress: ((String) -> Unit)? = null
    ): Result<GeneratedAutomotiveScript> {
        return try {
            val prompt = AutomotivePromptBuilder.buildPrompt(request)
            val config = generationConfig {
                responseMimeType = "application/json"
            }

            // Using official Firebase AI Logic SDK with supported Gemini Developer API model
            val model = Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = config,
                systemInstruction = com.google.firebase.ai.type.content {
                    text(AutomotivePromptBuilder.buildSystemInstruction())
                }
            )

            onProgress?.invoke("Connessione a Gemini 3.8 Flash...")

            val rawText = withTimeout(GENERATION_TIMEOUT_MS) {
                // Try progressive streaming first to reduce perceived latency and maintain an active socket
                var streamFailed = false
                val accumulated = StringBuilder()
                var chunkCount = 0

                try {
                    val stream = model.generateContentStream(prompt)
                    stream.collect { chunk ->
                        val piece = chunk.text.orEmpty()
                        if (piece.isNotEmpty()) {
                            accumulated.append(piece)
                            chunkCount++
                            onProgress?.invoke("Generazione in corso: $chunkCount blocchi ricevuti...")
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(TAG, "Streaming non riuscito, tentato fallback unario: ${e.message}")
                    streamFailed = true
                    if (accumulated.isNotEmpty()) {
                        // Se sono già stati ricevuti frammenti e la connessione si è interrotta, propaghiamo l'errore
                        throw e
                    }
                }

                if (!streamFailed && accumulated.isNotBlank()) {
                    accumulated.toString()
                } else {
                    onProgress?.invoke("Attesa completamento script da Gemini...")
                    val unaryResponse = model.generateContent(prompt)
                    unaryResponse.text.orEmpty()
                }
            }

            if (rawText.isBlank()) {
                throw IllegalStateException("Il modello Gemini ha restituito una risposta vuota.")
            }

            onProgress?.invoke("Validazione e parsing della sceneggiatura...")
            val parsed = parseJsonToScript(rawText)
            Result.success(parsed)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Generazione Firebase AI Logic scaduta dopo ${GENERATION_TIMEOUT_MS / 1000}s", e)
            Result.failure(
                IllegalStateException(
                    "La generazione dello script ha superato il tempo limite di ${GENERATION_TIMEOUT_MS / 1000} secondi. " +
                        "La risposta di Gemini ha impiegato troppo tempo o la connessione di rete è rallentata. Nessun progetto corrotto è stato salvato. Riprova."
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Errore durante la generazione Firebase AI Logic", e)
            val rawMsg = e.message.orEmpty()
            val friendlyMsg = when {
                rawMsg.contains("Firebase App Check token is invalid", ignoreCase = true) ||
                rawMsg.contains("App Check", ignoreCase = true) ||
                rawMsg.contains("AppCheck", ignoreCase = true) ||
                rawMsg.contains("app-check", ignoreCase = true) ||
                rawMsg.contains("403") ||
                rawMsg.contains("PERMISSION_DENIED", ignoreCase = true) -> {
                    val debugSecret = AppCheckHelper.getOrCreateDebugSecret(context)
                    "Attestazione Firebase App Check rifiutata dal server: il debug token del dispositivo non è ancora autorizzato.\n\n" +
                    "Debug Secret del tuo dispositivo:\n$debugSecret\n\n" +
                    "Passaggi per autorizzarlo direttamente dal tuo Samsung Galaxy S23:\n" +
                    "1. Copia il Debug Secret mostrato sopra (puoi farlo anche dalla sezione Impostazioni dell'app).\n" +
                    "2. Apri Chrome su console.firebase.google.com e seleziona il progetto 'luxdrive-ai-studio'.\n" +
                    "3. Vai nel menu -> App Check -> scheda 'App'.\n" +
                    "4. Clicca sui 3 puntini a destra di 'com.aistudio.luxdrive.zqkpmx' e seleziona 'Gestisci token di debug'.\n" +
                    "5. Clicca 'Aggiungi token di debug', assegna un nome (es. Galaxy S23) e incolla questo secret.\n" +
                    "6. Torna qui e tocca 'Genera con AI'."
                }
                rawMsg.contains("404") || rawMsg.contains("not found", ignoreCase = true) ->
                    "Modello Gemini non trovato o Firebase AI Logic non ancora abilitato nel progetto Firebase."
                rawMsg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) || rawMsg.contains("429") ->
                    "Quota delle richieste Gemini Developer API superata. Attendi qualche istante e riprova."
                rawMsg.contains("Unable to resolve host", ignoreCase = true) || rawMsg.contains("ConnectException", ignoreCase = true) ->
                    "Errore di connessione di rete: impossibile raggiungere i server Firebase. Controlla la connessione internet."
                else ->
                    e.localizedMessage ?: "Errore durante la generazione dello script con Firebase AI Logic."
            }
            Result.failure(IllegalStateException(friendlyMsg, e))
        }
    }

    fun parseJsonToScript(rawInput: String): GeneratedAutomotiveScript {
        val cleanJson = cleanMarkdownJson(rawInput)
        if (cleanJson.isBlank()) {
            throw IllegalStateException("La risposta del modello è vuota. Nessun progetto salvato.")
        }

        val json = try {
            JSONObject(cleanJson)
        } catch (e: Exception) {
            throw IllegalStateException("Risposta del modello incompleta o non conforme al formato JSON. Nessun progetto corrotto è stato salvato.", e)
        }

        val title = json.optString("title", "").trim()
        val fullScript = json.optString("fullScript", "").trim()
        val hook = json.optString("hook", "").trim()

        if (title.isBlank() && fullScript.isBlank() && hook.isBlank()) {
            throw IllegalStateException("La risposta JSON non contiene le sezioni essenziali del progetto video (titolo, hook o script). Nessun progetto corrotto è stato salvato.")
        }

        return GeneratedAutomotiveScript(
            title = if (title.isNotBlank()) title else "Progetto LuxDrive",
            youtubeTitleAlternative = json.optString("youtubeTitleAlternative", ""),
            hook = hook,
            fullScript = fullScript,
            voiceOverNotes = json.optString("voiceOverNotes", ""),
            researchAndSources = json.optString("researchAndSources", ""),
            storyboardSummary = json.optString("storyboardSummary", ""),
            capcutEditingNotes = json.optString("capcutEditingNotes", ""),
            description = json.optString("description", ""),
            chaptersText = json.optString("chaptersText", ""),
            tags = json.optString("tags", "luxdrive, automotive"),
            thumbnailConcept = json.optString("thumbnailConcept", ""),
            shortsDerivedScript = json.optString("shortsDerivedScript", ""),
            tiktokAdaptation = json.optString("tiktokAdaptation", ""),
            instagramReelAdaptation = json.optString("instagramReelAdaptation", ""),
            verifiedFactsNotice = json.optString("verifiedFactsNotice", "")
        )
    }

    private fun cleanMarkdownJson(input: String): String {
        var str = input.trim()
        if (str.startsWith("```json")) {
            str = str.removePrefix("```json")
        } else if (str.startsWith("```")) {
            str = str.removePrefix("```")
        }
        if (str.endsWith("```")) {
            str = str.removeSuffix("```")
        }
        return str.trim()
    }
}
