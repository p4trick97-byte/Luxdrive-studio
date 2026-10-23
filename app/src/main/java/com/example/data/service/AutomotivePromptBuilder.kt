package com.example.data.service

import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat

data class AiScriptRequest(
    val carTopic: String,
    val format: ProjectFormat = ProjectFormat.LONG_VIDEO,
    val durationMinutes: Int = 8,
    val contentGoal: String = "Analisi costi reali e valore di mercato",
    val category: AutomotiveCategory = AutomotiveCategory.SUPERCAR_MAINTENANCE_REAL_COST,
    val narrativeTone: String = "Autorevole & Analitico",
    val userSourcesOrNotes: String = ""
)

data class GeneratedAutomotiveScript(
    val title: String,
    val youtubeTitleAlternative: String,
    val hook: String,
    val fullScript: String,
    val voiceOverNotes: String,
    val researchAndSources: String,
    val storyboardSummary: String,
    val capcutEditingNotes: String,
    val description: String,
    val chaptersText: String,
    val tags: String,
    val thumbnailConcept: String,
    val shortsDerivedScript: String,
    val tiktokAdaptation: String,
    val instagramReelAdaptation: String,
    val verifiedFactsNotice: String
)

object AutomotivePromptBuilder {

    fun buildSystemInstruction(): String {
        return """
            Sei il capo autore, analista tecnico e consulente strategico del canale YouTube "LuxDrive Italia".
            LuxDrive è un canale premium italiano dedicato esclusivamente al mondo automotive: supercar, hypercar, auto sportive storiche, retroscena industriali e analisi rigorose dei costi di possesso e manutenzione.
            Identità visiva e stile del canale: Nero carbonio e oro metallico, elegante, professionale, autorevole, analitico e coinvolgente.

            REGOLE IMPRESCINDIBILI:
            1. LINGUA: Tutto il testo deve essere rigorosamente in italiano, naturale e professionale.
            2. ZERO INVENZIONI (RIGORE EPISTEMICO):
               - Non inventare mai dati tecnici, prezzi di listino, costi dei ricambi, valori di superbollo o prestazioni non confermate.
               - Devi sempre distinguere chiaramente:
                 * FATTI VERIFICATI E DOCUMENTATI (con riferimento a fonti ufficiali o listini)
                 * STIME E MEDIE DI MERCATO (es. svalutazione usata, consumi reali, preventivi kasko)
                 * IPOTESI E VALUTAZIONI EDITORIALI.
               - Suggerisci fonti primarie specifiche da consultare (ACI, registri storici, manuali d'officina ufficiali). Non fingere che fonti non verificate siano già state confermate.
            3. CALIBRAZIONE DELLA DURATA E COMPLETEZZA:
               - La velocità media di parlato in un video YouTube è di circa 130-150 parole al minuto.
               - Per un video lungo (es. 8 minuti), il campo fullScript DEVE essere un testo completo, approfondito e corposo (circa 800-1100 parole), strutturato in capitoli/sezioni chiari, con introduzione, sviluppo dei singoli blocchi, transizioni naturali e conclusione con call to action per l'iscrizione a LuxDrive. Non restituire riassunti di poche righe!
               - Per uno Short (9:16), genera uno script autonomo completo con hook istantaneo (primi 3 secondi), ritmo incalzante (30-60 secondi di parlato, circa 90-140 parole), indicazioni visive e chiusura netta.
            4. FORMATO DI RISPOSTA:
               - Restituisci esclusivamente un oggetto JSON valido, conforme allo schema richiesto, senza preamboli, senza saluti e senza blocchi di codice markdown aggiuntivi.
        """.trimIndent()
    }

    fun buildPrompt(request: AiScriptRequest): String {
        val estimatedWordTarget = if (request.format == ProjectFormat.LONG_VIDEO) {
            val minWords = (request.durationMinutes * 125).coerceAtLeast(600)
            val maxWords = (request.durationMinutes * 155).coerceAtLeast(800)
            "$minWords - $maxWords parole complessive per coprire circa ${request.durationMinutes} minuti di narrazione audio"
        } else {
            "90 - 140 parole per circa 45-60 secondi di video verticale"
        }

        return """
            Genera un progetto video automotive completo e professionale per il canale LuxDrive Italia con i seguenti parametri:

            PARAMETRI RICHIESTI:
            - Modello/Argomento: ${request.carTopic}
            - Formato video: ${request.format.displayName} (${if (request.format == ProjectFormat.LONG_VIDEO) "16:9 Orizzontale" else "9:16 Verticale"})
            - Durata richiesta: ${request.durationMinutes} minuti ($estimatedWordTarget)
            - Categoria editoriale: ${request.category.displayName}
            - Obiettivo del contenuto: ${request.contentGoal}
            - Tono narrativo: ${request.narrativeTone}
            ${if (request.userSourcesOrNotes.isNotBlank()) "- Note e fonti fornite dall'utente: ${request.userSourcesOrNotes}" else ""}

            REQUISITI DI STRUTTURA:
            1. Titolo YouTube principale magnetico ma credibile e titolo alternativo per A/B test.
            2. Hook iniziale (0-5 secondi) che catturi subito l'attenzione.
            3. Full Script in italiano esteso e completo, suddiviso per sezioni (INTRODUZIONE, SVILUPPO DETTAGLIATO, CONSIDERAZIONI ECONOMICHE/TECNICHE, CONCLUSIONE E CTA).
            4. Note per il voice-over (ritmo, pause, enfasi sui numeri).
            5. Storyboard scena per scena con minutaggio, note su inquadrature/b-roll dell'auto, testi a schermo e sound design.
            6. Istruzioni pratiche passo-passo per il montaggio in CapCut da smartphone (Samsung Galaxy/Android): formati, tagli pause, keyframe zoom su motore e cerchi, color grading cinematico scuro/oro e bilanciamento audio sottofondo a -22dB.
            7. Descrizione YouTube formattata, capitoli con timestamp (es: 0:00 Intro, ...) e tag pertinenti.
            8. Concept miniatura professionale ad alto contrasto con silhouette dell'auto e testo accattivante.
            9. Versione Shorts derivata (script autonomo 9:16), con note di adattamento per TikTok e Instagram Reels.
            10. Notifica di verifica delle fonti: elenco separato di [FATTI VERIFICATI], [STIME/MEDIE] e [IPOTESI EDITORIALI].

            Restituisci la risposta ESCLUSIVAMENTE come JSON con questa struttura esatta:
            {
              "title": "Titolo principale del video",
              "youtubeTitleAlternative": "Titolo alternativo per test A/B",
              "hook": "Testo esatto dell'hook iniziale per i primi secondi",
              "fullScript": "Sceneggiatura completa estesa in italiano con tutte le sezioni parlate...",
              "voiceOverNotes": "Indicazioni su tono, ritmo, pause e inflessione per la voce narrante",
              "researchAndSources": "Fonti primarie raccomandate da consultare e dati da verificare",
              "storyboardSummary": "Descrizione scena per scena con indicazioni di clip b-roll ed effetti visivi",
              "capcutEditingNotes": "Guida pratica step-by-step per montare il video in CapCut da smartphone",
              "description": "Descrizione completa per YouTube con call to action e link",
              "chaptersText": "0:00 Introduzione\n1:15 ... elenco capitoli con minutaggi",
              "tags": "luxdrive, supercar, tag1, tag2...",
              "thumbnailConcept": "Descrizione dettagliata della copertina stile nero e oro",
              "shortsDerivedScript": "Script autonomo per Short 9:16 di 45-60 secondi",
              "tiktokAdaptation": "Hook e istruzioni per la versione TikTok",
              "instagramReelAdaptation": "Concept e audio consigliato per Instagram Reels",
              "verifiedFactsNotice": "Classificazione trasparente: Fatti verificati, Stime calcolate e Ipotesi"
            }
        """.trimIndent()
    }
}
