package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.service.GeminiAutomotiveAiClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AutomotiveScriptParserTest {

    private lateinit var client: GeminiAutomotiveAiClient

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        client = GeminiAutomotiveAiClient(context)
    }

    @Test
    fun parseJsonToScript_parsesValidJsonSuccessfully() {
        val sampleJson = """
            {
              "title": "Mantenere una Ferrari 296 GTB: Il Conto Reale",
              "youtubeTitleAlternative": "Quanto Costa Davvero Guidare la 296 GTB in Italia?",
              "hook": "Pensavate che bastassero 300.000 euro? Ecco la fattura che nessuno vi mostra.",
              "fullScript": "Sezione 1: Benvenuti su LuxDrive Italia. Oggi analizziamo i costi reali...",
              "voiceOverNotes": "Tono calmo, enfasi sui costi dei tagliandi.",
              "researchAndSources": "Listino Ferrari 7 anni manutenzione inclusa, calcolo bollo ACI.",
              "storyboardSummary": "Scena 1: Dettaglio scarico centrale con audio sound.",
              "capcutEditingNotes": "Taglio dinamico sui bassi del V6 120 gradi.",
              "description": "Analisi approfondita sui costi di possesso Ferrari.",
              "chaptersText": "0:00 Intro\n1:20 Superbollo e Tasse",
              "tags": "luxdrive, ferrari, 296gtb, superbollo",
              "thumbnailConcept": "Sfondo nero carbonio con cornice oro e grafica bollo barrata.",
              "shortsDerivedScript": "Ecco quanto costa un set di freni carboceramici su questa Ferrari.",
              "tiktokAdaptation": "Video veloce con testo a comparsa dinamico.",
              "instagramReelAdaptation": "Estetica cinematica 4K scura con musica lo-fi lusso.",
              "verifiedFactsNotice": "Fatti verificati: tagliandi 7 anni inclusi; Stime: kasko 6000€/anno."
            }
        """.trimIndent()

        val parsed = client.parseJsonToScript(sampleJson)

        assertEquals("Mantenere una Ferrari 296 GTB: Il Conto Reale", parsed.title)
        assertEquals("Quanto Costa Davvero Guidare la 296 GTB in Italia?", parsed.youtubeTitleAlternative)
        assertTrue(parsed.hook.contains("300.000 euro"))
        assertTrue(parsed.fullScript.contains("LuxDrive Italia"))
        assertEquals("Tono calmo, enfasi sui costi dei tagliandi.", parsed.voiceOverNotes)
        assertTrue(parsed.storyboardSummary.contains("Scena 1"))
        assertTrue(parsed.capcutEditingNotes.contains("CapCut") || parsed.capcutEditingNotes.contains("Taglio dinamico"))
        assertTrue(parsed.chaptersText.contains("0:00 Intro"))
        assertTrue(parsed.verifiedFactsNotice.contains("Fatti verificati"))
    }

    @Test
    fun parseJsonToScript_stripsMarkdownFences() {
        val fencedJson = """
            ```json
            {
              "title": "Porsche 911 GT3 RS: Aerodinamica Estrema",
              "hook": "Un alettone che produce 860 kg di carico a 285 km/h.",
              "fullScript": "Testo completo della recensione tecnica..."
            }
            ```
        """.trimIndent()

        val parsed = client.parseJsonToScript(fencedJson)

        assertEquals("Porsche 911 GT3 RS: Aerodinamica Estrema", parsed.title)
        assertTrue(parsed.hook.contains("860 kg"))
        assertEquals("Testo completo della recensione tecnica...", parsed.fullScript)
    }

    @Test
    fun parseJsonToScript_fillsDefaultsWhenKeysMissing() {
        val minimalJson = """
            {
              "title": "Alfa Romeo Giulia GTAm"
            }
        """.trimIndent()

        val parsed = client.parseJsonToScript(minimalJson)

        assertEquals("Alfa Romeo Giulia GTAm", parsed.title)
        assertEquals("", parsed.hook)
        assertEquals("", parsed.fullScript)
        assertTrue(parsed.tags.contains("luxdrive"))
    }

    @Test(expected = IllegalStateException::class)
    fun parseJsonToScript_throwsOnMalformedJson() {
        client.parseJsonToScript("This is definitely not a json string")
    }

    @Test(expected = IllegalStateException::class)
    fun parseJsonToScript_throwsOnTruncatedJson() {
        client.parseJsonToScript("""{"title": "Ferrari 296 GTB", "hook": "Questo video """)
    }

    @Test(expected = IllegalStateException::class)
    fun parseJsonToScript_throwsOnEmptyString() {
        client.parseJsonToScript("   ")
    }

    @Test(expected = IllegalStateException::class)
    fun parseJsonToScript_throwsWhenEssentialFieldsMissing() {
        client.parseJsonToScript("""{"randomField": "test"}""")
    }

    @Test
    fun parsePhase1Json_and_parsePhase2Json_andMergeSuccessfully() {
        val phase1Json = """
            {
              "title": "Bugatti Tourbillon: 1800 CV V16 Aspirato",
              "youtubeTitleAlternative": "La Fine dei Turbo in Casa Bugatti?",
              "hook": "9000 giri senza turbo: l'orologeria meccanica applicata a un motore.",
              "fullScript": "Narrazione completa Fase 1 per LuxDrive...",
              "voiceOverNotes": "Tono solenne ed epico",
              "verifiedFactsNotice": "Fatti verificati: motore Cosworth 8.3L V16",
              "researchAndSources": "Comunicato stampa Bugatti Rimac"
            }
        """.trimIndent()

        val phase2Json = """
            {
              "storyboardSummary": "Scena 1: Dettaglio del quadro strumenti analogico in titanio",
              "capcutEditingNotes": "Zoom lento sul tachimetro meccanico, audio scarico a -22dB",
              "description": "Analisi approfondita della nuova hypercar Bugatti",
              "chaptersText": "0:00 Intro\n1:00 Il V16 Cosworth",
              "tags": "luxdrive, bugatti, tourbillon, v16",
              "thumbnailConcept": "Primo piano sul quadro strumenti stile orologio con accenti oro",
              "shortsDerivedScript": "Questo tachimetro costa come una Porsche 911.",
              "tiktokAdaptation": "Video dinamico con suono del V16 in primo piano",
              "instagramReelAdaptation": "Reel cinematico con montaggio ad alto contrasto"
            }
        """.trimIndent()

        val p1 = client.parsePhase1Json(phase1Json)
        val p2 = client.parsePhase2Json(phase2Json)
        val merged = client.mergeScriptPhases(p1, p2)

        assertEquals("Bugatti Tourbillon: 1800 CV V16 Aspirato", merged.title)
        assertEquals("La Fine dei Turbo in Casa Bugatti?", merged.youtubeTitleAlternative)
        assertTrue(merged.hook.contains("9000 giri"))
        assertTrue(merged.fullScript.contains("Fase 1"))
        assertEquals("Tono solenne ed epico", merged.voiceOverNotes)
        assertTrue(merged.verifiedFactsNotice.contains("Cosworth"))
        assertTrue(merged.storyboardSummary.contains("quadro strumenti"))
        assertTrue(merged.capcutEditingNotes.contains("CapCut") || merged.capcutEditingNotes.contains("tachimetro"))
        assertTrue(merged.chaptersText.contains("0:00 Intro"))
        assertTrue(merged.tags.contains("bugatti"))
        assertTrue(merged.shortsDerivedScript.contains("Porsche 911"))
    }

    @Test(expected = IllegalStateException::class)
    fun parsePhase1Json_throwsOnMissingEssentialFields() {
        client.parsePhase1Json("""{"randomField": "value"}""")
    }

    @Test(expected = IllegalStateException::class)
    fun parsePhase1Json_throwsOnEmpty() {
        client.parsePhase1Json("")
    }

    @Test(expected = IllegalStateException::class)
    fun parsePhase2Json_throwsOnEmpty() {
        client.parsePhase2Json("")
    }
}
