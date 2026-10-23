package com.example

import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat
import com.example.data.service.AiScriptRequest
import com.example.data.service.AutomotivePromptBuilder
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutomotivePromptBuilderTest {

    @Test
    fun systemInstruction_enforcesItalianAndEpistemicRigor() {
        val instruction = AutomotivePromptBuilder.buildSystemInstruction()

        assertTrue("Must require Italian language", instruction.contains("italiano"))
        assertTrue("Must mention LuxDrive Italia", instruction.contains("LuxDrive"))
        assertTrue("Must forbid inventing prices or specs", instruction.contains("Non inventare mai"))
        assertTrue("Must require verified facts", instruction.contains("FATTI VERIFICATI"))
        assertTrue("Must require market estimates", instruction.contains("STIME"))
        assertTrue("Must require editorial hypotheses", instruction.contains("IPOTESI"))
        assertTrue("Must mention speech speed 130-150 words per minute", instruction.contains("130-150 parole"))
        assertTrue("Must enforce JSON output", instruction.contains("JSON"))
    }

    @Test
    fun buildPrompt_forLongVideo_calibratesWordTargetAndIncludesCapCut() {
        val request = AiScriptRequest(
            carTopic = "Ferrari 296 GTB",
            format = ProjectFormat.LONG_VIDEO,
            durationMinutes = 8,
            contentGoal = "Analisi costi reali di gestione e superbollo",
            category = AutomotiveCategory.SUPERCAR_MAINTENANCE_REAL_COST,
            narrativeTone = "Autorevole & Analitico",
            userSourcesOrNotes = "Dati bollo ACI Regione Lombardia"
        )

        val prompt = AutomotivePromptBuilder.buildPrompt(request)

        assertTrue(prompt.contains("Ferrari 296 GTB"))
        assertTrue(prompt.contains("16:9 Orizzontale"))
        assertTrue(prompt.contains("8 minuti"))
        assertTrue(prompt.contains("parole complessive"))
        assertTrue(prompt.contains("CapCut"))
        assertTrue(prompt.contains("Dati bollo ACI"))
        assertTrue(prompt.contains("storyboardSummary"))
        assertTrue(prompt.contains("verifiedFactsNotice"))
    }

    @Test
    fun buildPrompt_forShorts_setsVerticalFormatAndCompactDuration() {
        val request = AiScriptRequest(
            carTopic = "Porsche 911 GT3 RS",
            format = ProjectFormat.SHORTS,
            durationMinutes = 1,
            contentGoal = "Hook sonoro aspirato a 9000 giri e aerodinamica DRS",
            category = AutomotiveCategory.SUPERCAR_AND_HYPERCAR,
            narrativeTone = "Emozionale & Diretto",
            userSourcesOrNotes = ""
        )

        val prompt = AutomotivePromptBuilder.buildPrompt(request)

        assertTrue(prompt.contains("Porsche 911 GT3 RS"))
        assertTrue(prompt.contains("9:16 Verticale"))
        assertTrue(prompt.contains("45-60 secondi"))
        assertFalse(prompt.contains("Note e fonti fornite dall'utente:"))
    }
}
