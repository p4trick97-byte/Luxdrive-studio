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

    suspend fun generateScript(request: AiScriptRequest): Result<GeneratedAutomotiveScript> = withContext(Dispatchers.IO) {
        val status = getBackendStatus()

        when (status) {
            is AiBackendStatus.FirebaseAiLogicActive -> {
                generateViaFirebaseAiLogic(request)
            }
            is AiBackendStatus.NotConfigured -> {
                Result.failure(AiNotConfiguredException(status.explanation))
            }
        }
    }

    private suspend fun generateViaFirebaseAiLogic(request: AiScriptRequest): Result<GeneratedAutomotiveScript> {
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

            val response = withTimeout(60_000L) {
                model.generateContent(prompt)
            }

            val rawText = response.text
            if (rawText.isNullOrBlank()) {
                throw IllegalStateException("Il modello Gemini ha restituito una risposta vuota.")
            }
            val parsed = parseJsonToScript(rawText)
            Result.success(parsed)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Generazione Firebase AI Logic scaduta dopo 60s", e)
            Result.failure(IllegalStateException("La generazione dello script ha superato il tempo limite di 60 secondi. Riprova."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Errore durante la generazione Firebase AI Logic", e)
            val rawMsg = e.message.orEmpty()
            val friendlyMsg = when {
                rawMsg.contains("403") || rawMsg.contains("AppCheck") || rawMsg.contains("app-check") ->
                    "Errore di attestazione App Check (HTTP 403). Assicurati che il debug secret sia registrato nei token di debug della console Firebase e che le regole App Check non blocchino le richieste in fase di sviluppo."
                rawMsg.contains("404") || rawMsg.contains("not found") ->
                    "Modello Gemini non trovato o Firebase AI Logic non ancora abilitato nel progetto Firebase."
                rawMsg.contains("RESOURCE_EXHAUSTED") || rawMsg.contains("429") ->
                    "Quota delle richieste Gemini Developer API superata. Attendi qualche istante e riprova."
                rawMsg.contains("Unable to resolve host") || rawMsg.contains("ConnectException") ->
                    "Errore di connessione di rete: impossibile raggiungere i server Firebase. Controlla la connessione internet."
                else ->
                    e.localizedMessage ?: "Errore durante la generazione dello script con Firebase AI Logic."
            }
            Result.failure(IllegalStateException(friendlyMsg, e))
        }
    }

    fun parseJsonToScript(rawInput: String): GeneratedAutomotiveScript {
        val cleanJson = cleanMarkdownJson(rawInput)
        val json = try {
            JSONObject(cleanJson)
        } catch (e: Exception) {
            throw IllegalStateException("Risposta del modello non conforme al formato JSON richiesto.", e)
        }

        return GeneratedAutomotiveScript(
            title = json.optString("title", "Progetto LuxDrive"),
            youtubeTitleAlternative = json.optString("youtubeTitleAlternative", ""),
            hook = json.optString("hook", ""),
            fullScript = json.optString("fullScript", ""),
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
