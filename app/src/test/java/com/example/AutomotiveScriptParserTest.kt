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
}
