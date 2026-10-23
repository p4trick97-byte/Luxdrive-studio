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
            3. CALIBRAZIONE DELLA DURATA E COMPLETEZZA SENZA RIDONDANZE:
               - La velocità media di parlato in un video YouTube è di circa 130-150 parole al minuto.
               - Per un video lungo (es. 8 minuti), il campo fullScript deve essere completo, approfondito e incisivo (circa 350-550 parole ben strutturate), diviso in sezioni con introduzione, sviluppo analitico dei costi/prestazioni e conclusione con call to action per l'iscrizione a LuxDrive.
               - Per uno Short (9:16), genera uno script autonomo con hook istantaneo nei primi 3 secondi (ritmo incalzante per 30-60 secondi di parlato, circa 90-130 parole) e chiusura netta.
            4. FORMATO DI RISPOSTA:
               - Restituisci esclusivamente un oggetto JSON valido e completo, conforme allo schema richiesto, senza preamboli, senza saluti e senza blocchi di codice markdown aggiuntivi.
        """.trimIndent()
    }

    fun buildPrompt(request: AiScriptRequest): String {
        val estimatedWordTarget = if (request.format == ProjectFormat.LONG_VIDEO) {
            val targetWords = (request.durationMinutes * 50).coerceIn(350, 600)
            "circa $targetWords parole complessive per il campo fullScript, calibrate per ${request.durationMinutes} minuti"
        } else {
            "circa 90 - 130 parole per 45-60 secondi di video verticale"
        }

        return """
            Genera un progetto video automotive completo e professionale per il canale YouTube LuxDrive Italia:

            PARAMETRI RICHIESTI:
            - Modello/Argomento: ${request.carTopic}
            - Formato video: ${request.format.displayName} (${if (request.format == ProjectFormat.LONG_VIDEO) "16:9 Orizzontale" else "9:16 Verticale"})
            - Durata richiesta: ${request.durationMinutes} minuti ($estimatedWordTarget)
            - Categoria editoriale: ${request.category.displayName}
            - Obiettivo del contenuto: ${request.contentGoal}
            - Tono narrativo: ${request.narrativeTone}
            ${if (request.userSourcesOrNotes.isNotBlank()) "- Note e fonti fornite dall'utente: ${request.userSourcesOrNotes}" else ""}

            REQUISITI DI STRUTTURA (completi, densi di valore ma senza prolissità):
            1. Titolo YouTube principale magnetico ma credibile e titolo alternativo per A/B test.
            2. Hook iniziale (0-5 secondi) che catturi subito l'attenzione.
            3. fullScript: narrazione completa in italiano, articolata in sezioni chiare (INTRODUZIONE, SVILUPPO DETTAGLIATO, CONSIDERAZIONI ECONOMICHE/TECNICHE, CONCLUSIONE con invito all'iscrizione a LuxDrive).
            4. voiceOverNotes: indicazioni sintetiche su ritmo, pause ed enfasi.
            5. storyboardSummary: 4-6 scene ordinate con indicazioni di b-roll dell'auto ed effetti sonori.
            6. capcutEditingNotes: istruzioni pratiche passo-passo per il montaggio in CapCut da smartphone (formati, tagli pause, zoom su dettagli e audio a -22dB).
            7. description: descrizione pronta per YouTube, chaptersText con minutaggi (es: 0:00 Intro...) e tags pertinenti.
            8. thumbnailConcept: idea copertina ad alto contrasto in stile nero carbonio e oro.
            9. shortsDerivedScript: versione autonoma 9:16 di 30-45 secondi, con note rapide di adattamento per TikTok e Instagram Reels.
            10. verifiedFactsNotice: classificazione sintetica tra [FATTI VERIFICATI], [STIME/MEDIE] e [IPOTESI].

            Restituisci la risposta ESCLUSIVAMENTE come JSON valido con questa struttura esatta:
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

    fun buildPhase1Prompt(request: AiScriptRequest): String {
        val estimatedWordTarget = if (request.format == ProjectFormat.LONG_VIDEO) {
            val targetWords = (request.durationMinutes * 45).coerceIn(300, 500)
            "circa $targetWords parole per il testo parlato (fullScript)"
        } else {
            "circa 90 - 130 parole per 45-60 secondi di video verticale"
        }

        return """
            FASE 1/2: Genera la sceneggiatura principale, l'hook e le note di narrazione per il video LuxDrive Italia.

            PARAMETRI:
            - Modello/Argomento: ${request.carTopic}
            - Formato: ${request.format.displayName} (${if (request.format == ProjectFormat.LONG_VIDEO) "16:9 Orizzontale" else "9:16 Verticale"})
            - Durata richiesta: ${request.durationMinutes} minuti ($estimatedWordTarget)
            - Categoria: ${request.category.displayName}
            - Obiettivo: ${request.contentGoal}
            - Tono narrativo: ${request.narrativeTone}
            ${if (request.userSourcesOrNotes.isNotBlank()) "- Note e fonti fornite dall'utente: ${request.userSourcesOrNotes}" else ""}

            REQUISITI SPECIFICI FASE 1:
            1. Titolo principale potente e titolo alternativo A/B.
            2. Hook magnetico per i primi 3-5 secondi.
            3. fullScript: narrazione parlata completa divisa in sezioni (INTRO, SVILUPPO TECNICO/COSTI, CONCLUSIONE con invito ad iscriversi a LuxDrive).
            4. voiceOverNotes: indicazioni concise su tono, ritmo ed enfasi per la registrazione.
            5. verifiedFactsNotice: distinzione trasparente tra [FATTI VERIFICATI], [STIME/MEDIE] e [IPOTESI].
            6. researchAndSources: fonti primarie ufficiali (ACI, registri, manuali).

            Restituisci ESCLUSIVAMENTE un JSON valido con questa struttura:
            {
              "title": "Titolo principale del video",
              "youtubeTitleAlternative": "Titolo alternativo per test A/B",
              "hook": "Hook iniziale per i primi 3-5 secondi",
              "fullScript": "Sceneggiatura parlata completa in italiano...",
              "voiceOverNotes": "Indicazioni per il doppiaggio/voce",
              "verifiedFactsNotice": "Classificazione tra Fatti verificati, Stime calcolate e Ipotesi",
              "researchAndSources": "Fonti primarie raccomandate da consultare"
            }
        """.trimIndent()
    }

    fun buildPhase2Prompt(request: AiScriptRequest, title: String, hook: String): String {
        return """
            FASE 2/2: Genera gli asset di produzione video, montaggio CapCut e distribuzione social basati sulla sceneggiatura approvata.

            CONTESTO DEL PROGETTO:
            - Titolo: $title
            - Hook: $hook
            - Modello/Argomento: ${request.carTopic}
            - Formato: ${request.format.displayName}
            - Tono: ${request.narrativeTone}

            REQUISITI SPECIFICI FASE 2:
            1. storyboardSummary: 4-6 scene ordinate con indicazioni precise di b-roll ed effetti visivi.
            2. capcutEditingNotes: guida step-by-step per montare il video in CapCut da smartphone (transizioni, tagli pause, testo e audio a -22dB).
            3. description: descrizione pronta per YouTube con riassunto accattivante e call to action.
            4. chaptersText: minutaggi ordinati (es. 0:00 Intro\n...).
            5. tags: tag separati da virgola per YouTube.
            6. thumbnailConcept: concept copertina ad alto contrasto (stile nero carbonio e oro).
            7. shortsDerivedScript: versione autonoma verticale 9:16 di 30-45 secondi con ritmo rapido.
            8. tiktokAdaptation: hook specifico e istruzioni per TikTok.
            9. instagramReelAdaptation: stile visivo e musica consigliata per Instagram Reels.

            Restituisci ESCLUSIVAMENTE un JSON valido con questa struttura:
            {
              "storyboardSummary": "Descrizione scena per scena con clip b-roll ed effetti visivi",
              "capcutEditingNotes": "Guida pratica per il montaggio in CapCut da smartphone",
              "description": "Descrizione completa per YouTube con call to action",
              "chaptersText": "0:00 Introduzione\n1:15 ...",
              "tags": "luxdrive, ferrari, supercar...",
              "thumbnailConcept": "Idea grafica copertina stile nero e oro",
              "shortsDerivedScript": "Script autonomo per Short 9:16",
              "tiktokAdaptation": "Hook e istruzioni per TikTok",
              "instagramReelAdaptation": "Concept per Instagram Reels"
            }
        """.trimIndent()
    }
}
