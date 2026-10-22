package com.example.data.repository

import com.example.data.local.VideoProjectDao
import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VideoProjectRepository(private val dao: VideoProjectDao) {

    val allProjects: Flow<List<VideoProject>> = dao.getAllProjects()
    val scheduledProjects: Flow<List<VideoProject>> = dao.getScheduledProjects()

    fun searchProjects(query: String): Flow<List<VideoProject>> = dao.searchProjects(query)

    suspend fun getProjectById(id: Long): VideoProject? = dao.getProjectById(id)

    suspend fun saveProject(project: VideoProject): Long {
        return if (project.id == 0L) {
            dao.insertProject(project.copy(createdAtMillis = System.currentTimeMillis(), updatedAtMillis = System.currentTimeMillis()))
        } else {
            dao.updateProject(project.copy(updatedAtMillis = System.currentTimeMillis()))
            project.id
        }
    }

    suspend fun duplicateProject(originalId: Long): Long? {
        val original = dao.getProjectById(originalId) ?: return null
        val copy = original.copy(
            id = 0L,
            title = "${original.title} (Copia)",
            status = ProjectStatus.DRAFT.name,
            scheduledDateMillis = null,
            publishedDateMillis = null,
            youtubeVideoId = "",
            createdAtMillis = System.currentTimeMillis(),
            updatedAtMillis = System.currentTimeMillis()
        )
        return dao.insertProject(copy)
    }

    suspend fun deleteProject(id: Long) {
        dao.deleteProjectById(id)
    }

    suspend fun seedInitialProjectsIfEmpty() {
        if (dao.getCount() == 0) {
            val now = System.currentTimeMillis()
            val dayMillis = 86_400_000L

            val p1 = VideoProject(
                title = "Quanto Costa Davvero Mantenere una Ferrari 488 GTB in Italia?",
                category = AutomotiveCategory.SUPERCAR_MAINTENANCE_REAL_COST.displayName,
                format = ProjectFormat.LONG_VIDEO.name,
                status = ProjectStatus.PUBLISHED.name,
                scheduledDateMillis = now - (7 * dayMillis),
                publishedDateMillis = now - (7 * dayMillis),
                youtubeVideoId = "LX-488-ITALIA",
                targetAudience = "Appassionati di supercar, proprietari aspirazionali e pubblico incuriosito dai costi reali.",
                hook = "Tutti vedono la supercar lucida al bar, ma nessuno parla del bonifico da 7.400€ per il superbollo o del tagliando annuale. Oggi apriamo i conti reali di una Ferrari 488 GTB.",
                researchAndSources = "- Dati ACI su Superbollo e Bollo Ordinario (Regione Lombardia / Emilia)\n- Tabelle pneumatici Michelin Pilot Sport Cup 2 (omologazione K1 Ferrari)\n- Intervista a officina autorizzata Maranello Service\n- Svalutazione media mercato Autoscout24 2021-2024",
                fullScript = """
INTRO (0:00 - 1:15)
Hook ad alto impatto: 'Comprare una Ferrari 488 usata oggi costa circa 180.000€. Sembra una cifra raggiungibile per un sogno. Ma sai quanto devi versare ogni 365 giorni solo per lasciarla ferma in garage?'

BLOCCO 1: TASSE FISSE DELLO STATO (1:15 - 4:00)
- Potenza: 670 CV (492 kW).
- I primi 185 kW: bollo ordinario ~1.100€.
- Eccedenza superbollo: 307 kW x 20€ = 6.140€.
- Totale annuo solo di tasse statiche: ~7.240€!

BLOCCO 2: ASSICURAZIONE E COPERTURA CRISTALLI / KASKO (4:00 - 6:30)
- Polizza incendio/furto su valore residuo con localizzatore satellitare obbligatorio.
- Franchigia media cristalli (un parabrezza con sensori costa 4.500€).
- Costo annuo polizza completa: dai 3.200€ ai 5.500€.

BLOCCO 3: TAGLIANDI E CONSUMABILI (6:30 - 9:30)
- Piano Genuine Maintenance 7 anni (se ancora valido o decaduto).
- Cambio dischi carboceramica CCM: costo kit 14.000€ (durata circa 80.000 km in uso stradale).
- Gomme: treno posteriore da 305/30 R20 dura circa 8.000-10.000 km. Spesa: 1.600€.

CONCLUSIONI E VERDETTO FINALE (9:30 - 12:00)
- Costo chilometrico medio stimato: 4,20€/km.
- Budget annuo raccomandato senza imprevisti: 14.000€ - 18.000€.
- Call to Action finale: iscriviti a LuxDrive e commenta quale supercar vuoi analizzare!
                """.trimIndent(),
                voiceOverNotes = "Tono autorevole, calmo ma dinamico. Nessun sensazionalismo finto, precisione maniacale sulle cifre.",
                capcutEditingNotes = "Transizioni rapide su scontrini e fatture grafiche. Sound effect 'cash register' sommesso. B-roll rallentati su cerchi e scarico.",
                estimatedDurationMinutes = 12,
                youtubeTitleAlternative = "Ferrari 488 GTB: La Verità sui Costi di Gestione che Nessuno Dice!",
                description = "Abbiamo fatto i conti al centesimo su cosa significa mantenere una Ferrari 488 GTB in Italia tra bollo, superbollo, assicurazione kasko, tagliandi e gomme. Iscriviti a LuxDrive per altri report esclusivi!",
                chaptersText = "0:00 Intro e il mito del prezzo d'acquisto\n1:15 Il salasso di Bollo e Superbollo\n4:00 Assicurazione Kasko e localizzatore\n6:30 Freni carboceramica e pneumatici\n9:30 Il bilancio annuo finale",
                tags = "ferrari 488, costi mantenimento ferrari, superbollo ferrari, quanto costa mantenere una supercar, ferrari italia, luxdrive",
                thumbnailConcept = "Ferrari 488 nera su sfondo studio scuro. Freccia dorata con testo bold '7.240€ SOLO DI TASSE!'. Contrasto altissimo.",
                shortsDerivedScript = "Sai quanto costa tenere ferma questa Ferrari 488 in garage per un anno? 7.240€ di solo bollo e superbollo prima ancora di accendere il motore!",
                tiktokAdaptation = "Hook 0-3s: 'Non comprare questa Ferrari finché non vedi questo numero'. Mostra tabella ACI a schermo.",
                instagramReelAdaptation = "Reel carosello video con beat drop all'accensione del V8 biturbo e breakdown delle spese in sovraimpressione.",
                viewsCount = 42800L,
                watchHours = 2410.5,
                ctrPercent = 8.6,
                likesCount = 2890L,
                commentsCount = 312L,
                isDemoAnalytics = true
            )

            val p2 = VideoProject(
                title = "Bugatti EB110: Il Sogno Italiano di Campogalliano Spezzato",
                category = AutomotiveCategory.AUTOMOTIVE_STORIES_AND_BACKSTAGE.displayName,
                format = ProjectFormat.LONG_VIDEO.name,
                status = ProjectStatus.READY.name,
                scheduledDateMillis = now + (3 * dayMillis),
                targetAudience = "Amanti della storia dell'automobile, appassionati degli anni '90 e dell'ingegneria italiana.",
                hook = "Nel 1991, nel cuore dell'Emilia Romagna, un uomo visionario di nome Romano Artioli sfidò l'intero pianeta costruendo la fabbrica più moderna al mondo per far rinascere la Bugatti.",
                researchAndSources = "- Libro 'Bugatti & Lotus Thriller' di Romano Artioli\n- Testimonianze ingegner Paolo Stanzani e designer Marcello Gandini\n- Documenti d'archivio della Fabbrica Blu di Campogalliano (Modena)",
                fullScript = "La storia completa della Fabbrica Blu, dalla genesi del motore V12 quadriturbo e trazione integrale fino al misterioso sabotaggio e al fallimento del 1995...",
                voiceOverNotes = "Tono narrativo e nostalgico, ritmo in crescendo.",
                capcutEditingNotes = "Effetto grana vintage su immagini d'archivio '90, contrasto nitido sulle riprese moderne di Campogalliano.",
                estimatedDurationMinutes = 16,
                youtubeTitleAlternative = "La Vera Storia della Fabbrica Blu: Come Distrussero la Bugatti Italiana",
                description = "La storia incredibile e drammatica della Bugatti EB110 e della Fabbrica Blu di Campogalliano. Tra innovazioni leggendarie e colpi bassi industriali.",
                chaptersText = "0:00 Il sogno di Romano Artioli\n3:20 La nascita della Fabbrica Blu\n7:10 V12 Quadriturbo e telaio in carbonio\n11:45 Le oscure manovre dei concorrenti\n14:30 L'abbandono e l'eredità",
                tags = "bugatti eb110, campogalliano, romano artioli, fabbrica blu, supercar anni 90, storia motori",
                thumbnailConcept = "EB110 blu davanti alla facciata abbandonata di Campogalliano. Testo oro: 'L'HANNO SABOTATA?'",
                shortsDerivedScript = "La Bugatti più folle della storia non è nata in Francia, ma in un capannone a 15 minuti da Modena. Ecco cosa accadde...",
                tiktokAdaptation = "Video veloce con foto prima/dopo della Fabbrica Blu.",
                instagramReelAdaptation = "Sound ambient drammatico, focus su dettagli stilistici di Gandini.",
                viewsCount = 0L,
                watchHours = 0.0,
                ctrPercent = 0.0,
                likesCount = 0L,
                commentsCount = 0L,
                isDemoAnalytics = false
            )

            val p3 = VideoProject(
                title = "Shorts: Il Tagliando Folle da 25.000€ della Bugatti Veyron!",
                category = AutomotiveCategory.MAINTENANCE_AND_OWNERSHIP_COSTS.displayName,
                format = ProjectFormat.SHORTS.name,
                status = ProjectStatus.SCHEDULED.name,
                scheduledDateMillis = now + (1 * dayMillis),
                targetAudience = "Pubblico generalista affascinato da numeri record ed estremi dell'automobilismo.",
                hook = "Quanto costa cambiare l'olio su una Bugatti Veyron? Più di una Volkswagen Golf nuova!",
                researchAndSources = "- Manuale tecnico Bugatti W16\n- 16 tappi di scolo e smontaggio completo passaruota posteriori",
                fullScript = "Per fare un normale tagliando su una Veyron servono 27 ore di manodopera, smontare i parafanghi posteriori e svuotare 16 diversi tappi di scolo per il radiatore dell'olio!",
                voiceOverNotes = "Ritmo incalzante, durata esatta 45 secondi.",
                capcutEditingNotes = "Sottotitoli dinamici giallo oro e bianco, transizioni zoom in/out ogni 2.5 secondi.",
                estimatedDurationMinutes = 1,
                youtubeTitleAlternative = "25.000€ per un Cambio Olio?! La Follia della Bugatti Veyron",
                description = "I costi folli di manutenzione del motore W16 Bugatti. #LuxDrive #Shorts #Bugatti #Supercar",
                chaptersText = "",
                tags = "shorts, bugatti, veyron, tagliando auto, curiosita auto",
                thumbnailConcept = "Dettaglio motore W16 con scritta rossa '25.000€ PER UN CAMBIO OLIO!'",
                shortsDerivedScript = "Già pronto in formato 9:16 nativo.",
                tiktokAdaptation = "Testo trending: 'POV: porti la tua Bugatti a fare il tagliando'.",
                instagramReelAdaptation = "Reel virale con stickers interrogativi.",
                viewsCount = 0L,
                watchHours = 0.0,
                ctrPercent = 0.0,
                likesCount = 0L,
                commentsCount = 0L,
                isDemoAnalytics = false
            )

            dao.insertProject(p1)
            dao.insertProject(p2)
            dao.insertProject(p3)
        }
    }
}
