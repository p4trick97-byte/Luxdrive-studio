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

data class GeminiTestResult(
    val isSuccess: Boolean,
    val modelName: String,
    val durationMs: Long,
    val responseText: String? = null,
    val errorMessage: String? = null
)

data class Phase1Result(
    val title: String,
    val youtubeTitleAlternative: String,
    val hook: String,
    val fullScript: String,
    val voiceOverNotes: String,
    val verifiedFactsNotice: String,
    val researchAndSources: String
)

data class Phase2Result(
    val storyboardSummary: String,
    val capcutEditingNotes: String,
    val description: String,
    val chaptersText: String,
    val tags: String,
    val thumbnailConcept: String,
    val shortsDerivedScript: String,
    val tiktokAdaptation: String,
    val instagramReelAdaptation: String
)

class GeminiAutomotiveAiClient(private val context: Context) {

    companion object {
        const val DEFAULT_MODEL = "gemini-3.5-flash-lite"
        const val ALTERNATIVE_MODEL = "gemini-3.8-flash"
    }

    fun getDefaultModel(): String = DEFAULT_MODEL

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

    suspend fun testConnection(modelName: String = DEFAULT_MODEL): GeminiTestResult = withContext(Dispatchers.IO) {
        val status = getBackendStatus()
        if (status is AiBackendStatus.NotConfigured) {
            return@withContext GeminiTestResult(
                isSuccess = false,
                modelName = modelName,
                durationMs = 0L,
                errorMessage = status.explanation
            )
        }

        val startTime = System.currentTimeMillis()
        try {
            val model = Firebase.ai.generativeModel(
                modelName = modelName
            )
            val response = withTimeout(30_000L) {
                model.generateContent("Rispondi esclusivamente con OK")
            }
            val durationMs = System.currentTimeMillis() - startTime
            val text = response.text?.trim().orEmpty()

            if (text.isBlank()) {
                GeminiTestResult(
                    isSuccess = false,
                    modelName = modelName,
                    durationMs = durationMs,
                    errorMessage = "Risposta vuota ricevuta dal modello $modelName."
                )
            } else {
                GeminiTestResult(
                    isSuccess = true,
                    modelName = modelName,
                    durationMs = durationMs,
                    responseText = text
                )
            }
        } catch (e: TimeoutCancellationException) {
            val durationMs = System.currentTimeMillis() - startTime
            GeminiTestResult(
                isSuccess = false,
                modelName = modelName,
                durationMs = durationMs,
                errorMessage = "Timeout richiesta minima (30s superati) per il modello $modelName."
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val durationMs = System.currentTimeMillis() - startTime
            Log.e(TAG, "Test connessione fallito per $modelName", e)
            val rawMsg = e.localizedMessage ?: e.message ?: e::class.java.simpleName
            GeminiTestResult(
                isSuccess = false,
                modelName = modelName,
                durationMs = durationMs,
                errorMessage = rawMsg
            )
        }
    }

    suspend fun generateScript(
        request: AiScriptRequest,
        modelName: String = DEFAULT_MODEL,
        onProgress: ((String) -> Unit)? = null
    ): Result<GeneratedAutomotiveScript> = withContext(Dispatchers.IO) {
        val status = getBackendStatus()

        when (status) {
            is AiBackendStatus.FirebaseAiLogicActive -> {
                generateViaFirebaseAiLogic(request, modelName, onProgress)
            }
            is AiBackendStatus.NotConfigured -> {
                Result.failure(AiNotConfiguredException(status.explanation))
            }
        }
    }

    private suspend fun generateViaFirebaseAiLogic(
        request: AiScriptRequest,
        modelName: String = DEFAULT_MODEL,
        onProgress: ((String) -> Unit)? = null
    ): Result<GeneratedAutomotiveScript> {
        return try {
            val config = generationConfig {
                responseMimeType = "application/json"
            }

            // Using the verified operational model gemini-3.5-flash-lite for all generation phases
            val model = Firebase.ai.generativeModel(
                modelName = modelName,
                generationConfig = config,
                systemInstruction = com.google.firebase.ai.type.content {
                    text(AutomotivePromptBuilder.buildSystemInstruction())
                }
            )

            // FASE 1: Sceneggiatura principale, hook e narrazione con gemini-3.5-flash-lite
            onProgress?.invoke("Fase 1/2: Generazione sceneggiatura e hook con Gemini 3.5 Flash Lite...")
            val phase1Prompt = AutomotivePromptBuilder.buildPhase1Prompt(request)

            val phase1Raw = withTimeout(60_000L) {
                val res = model.generateContent(phase1Prompt)
                res.text.orEmpty()
            }

            if (phase1Raw.isBlank()) {
                throw IllegalStateException("Fase 1 non riuscita: risposta vuota ricevuta da Gemini.")
            }

            val phase1Result = parsePhase1Json(phase1Raw)

            // FASE 2: Storyboard, CapCut e asset social basati sul titolo e hook confermati con gemini-3.5-flash-lite
            onProgress?.invoke("Fase 2/2: Generazione storyboard, CapCut e asset social con Gemini 3.5 Flash Lite...")
            val phase2Prompt = AutomotivePromptBuilder.buildPhase2Prompt(
                request = request,
                title = phase1Result.title,
                hook = phase1Result.hook
            )

            val phase2Raw = withTimeout(60_000L) {
                val res = model.generateContent(phase2Prompt)
                res.text.orEmpty()
            }

            if (phase2Raw.isBlank()) {
                throw IllegalStateException("Fase 2 non riuscita: risposta vuota ricevuta da Gemini per storyboard e CapCut.")
            }

            val phase2Result = parsePhase2Json(phase2Raw)

            onProgress?.invoke("Validazione e assemblaggio finale del progetto...")
            val fullScript = mergeScriptPhases(phase1Result, phase2Result)
            Result.success(fullScript)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Generazione Firebase AI Logic scaduta", e)
            Result.failure(
                IllegalStateException(
                    "La richiesta a Gemini ($modelName) ha superato il tempo limite consentito. " +
                        "La risposta ha impiegato troppo tempo o la connessione di rete è rallentata. Nessun progetto corrotto è stato salvato. Riprova."
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Errore durante la generazione Firebase AI Logic", e)
            val rawMsg = e.message.orEmpty()
            val friendlyMsg = when {
                rawMsg.contains("DEADLINE_EXCEEDED", ignoreCase = true) ||
                rawMsg.contains("Deadline exceeded", ignoreCase = true) ->
                    "Timeout server Gemini (DEADLINE_EXCEEDED durante il decode). La richiesta è ora suddivisa in due fasi per ridurre i token per richiesta. Riprova: se il carico persiste, effettua prima un test di connessione nelle Impostazioni."
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

    fun parsePhase1Json(rawInput: String): Phase1Result {
        val cleanJson = cleanMarkdownJson(rawInput)
        if (cleanJson.isBlank()) {
            throw IllegalStateException("La risposta della Fase 1 è vuota. Nessun progetto salvato.")
        }

        val json = try {
            JSONObject(cleanJson)
        } catch (e: Exception) {
            throw IllegalStateException("Risposta della Fase 1 non conforme al formato JSON. Nessun progetto corrotto è stato salvato.", e)
        }

        val title = json.optString("title", "").trim()
        val fullScript = json.optString("fullScript", "").trim()
        val hook = json.optString("hook", "").trim()

        if (title.isBlank() && fullScript.isBlank() && hook.isBlank()) {
            throw IllegalStateException("La risposta JSON della Fase 1 non contiene titolo, hook o script.")
        }

        return Phase1Result(
            title = if (title.isNotBlank()) title else "Progetto LuxDrive",
            youtubeTitleAlternative = json.optString("youtubeTitleAlternative", ""),
            hook = hook,
            fullScript = fullScript,
            voiceOverNotes = json.optString("voiceOverNotes", ""),
            verifiedFactsNotice = json.optString("verifiedFactsNotice", ""),
            researchAndSources = json.optString("researchAndSources", "")
        )
    }

    fun parsePhase2Json(rawInput: String): Phase2Result {
        val cleanJson = cleanMarkdownJson(rawInput)
        if (cleanJson.isBlank()) {
            throw IllegalStateException("La risposta della Fase 2 è vuota. Nessun progetto salvato.")
        }

        val json = try {
            JSONObject(cleanJson)
        } catch (e: Exception) {
            throw IllegalStateException("Risposta della Fase 2 non conforme al formato JSON. Nessun progetto corrotto è stato salvato.", e)
        }

        return Phase2Result(
            storyboardSummary = json.optString("storyboardSummary", ""),
            capcutEditingNotes = json.optString("capcutEditingNotes", ""),
            description = json.optString("description", ""),
            chaptersText = json.optString("chaptersText", ""),
            tags = json.optString("tags", "luxdrive, automotive"),
            thumbnailConcept = json.optString("thumbnailConcept", ""),
            shortsDerivedScript = json.optString("shortsDerivedScript", ""),
            tiktokAdaptation = json.optString("tiktokAdaptation", ""),
            instagramReelAdaptation = json.optString("instagramReelAdaptation", "")
        )
    }

    fun mergeScriptPhases(p1: Phase1Result, p2: Phase2Result): GeneratedAutomotiveScript {
        return GeneratedAutomotiveScript(
            title = p1.title,
            youtubeTitleAlternative = p1.youtubeTitleAlternative,
            hook = p1.hook,
            fullScript = p1.fullScript,
            voiceOverNotes = p1.voiceOverNotes,
            researchAndSources = p1.researchAndSources,
            storyboardSummary = p2.storyboardSummary,
            capcutEditingNotes = p2.capcutEditingNotes,
            description = p2.description,
            chaptersText = p2.chaptersText,
            tags = p2.tags,
            thumbnailConcept = p2.thumbnailConcept,
            shortsDerivedScript = p2.shortsDerivedScript,
            tiktokAdaptation = p2.tiktokAdaptation,
            instagramReelAdaptation = p2.instagramReelAdaptation,
            verifiedFactsNotice = p1.verifiedFactsNotice
        )
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
