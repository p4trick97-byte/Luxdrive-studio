package com.example.data.repository

import com.example.data.model.AiStrategyProposal
import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AiStrategyRepository {

    private val _proposals = MutableStateFlow<List<AiStrategyProposal>>(emptyList())
    val proposals: Flow<List<AiStrategyProposal>> = _proposals.asStateFlow()

    init {
        _proposals.value = generateDefaultProposals()
    }

    fun getProposalById(id: String): AiStrategyProposal? {
        return _proposals.value.firstOrNull { it.id == id }
    }

    fun filterByCategory(category: AutomotiveCategory?): List<AiStrategyProposal> {
        if (category == null) return _proposals.value
        return _proposals.value.filter { it.category == category }
    }

    fun generateNewProposalsForChannel(
        mostViewedCategory: String,
        channelRetentionRate: Double
    ): List<AiStrategyProposal> {
        val baseList = generateDefaultProposals()
        // If retention is high on costs or stories, prioritize those categories
        val sorted = baseList.sortedByDescending { proposal ->
            var score = 0
            if (proposal.category.displayName.contains("mantenere", ignoreCase = true) ||
                proposal.category.displayName.contains("costi", ignoreCase = true)
            ) {
                score += 3
            }
            if (proposal.category.displayName.contains("Storie", ignoreCase = true)) {
                score += 2
            }
            score
        }
        _proposals.value = sorted
        return sorted
    }

    private fun generateDefaultProposals(): List<AiStrategyProposal> {
        return listOf(
            AiStrategyProposal(
                id = "prop_1_mantenimento",
                category = AutomotiveCategory.SUPERCAR_MAINTENANCE_REAL_COST,
                provisionalTitle = "Quanto Costa Mantenere una Porsche 911 GT3 (992) per 10.000 km all'Anno?",
                coreIdea = "Scomposizione rigorosa e trasparente delle spese reali di una GT3: assicurazione, treni di Michelin Cup 2, freni in acciaio vs carboceramica PCCB e svalutazione al netto della bolla.",
                potentialAudience = "Appassionati del marchio Porsche, acquirenti indecisi tra Carrera GTS e GT3, e pubblico curioso di scoprire i costi vivi dei track day.",
                initialHook = "Una Porsche 911 GT3 usata parte da 190.000 euro. Ma se la guidi davvero per 10.000 chilometri l'anno, compresi due turni a Monza, quanto ti prosciuga il conto in banca?",
                recommendedFormat = ProjectFormat.LONG_VIDEO,
                suggestedDuration = "13 - 15 minuti",
                sourcesToVerify = listOf(
                    "Listino Centro Porsche Milano/Padova per Service Intermedio",
                    "Portali assicurativi con franchigia pista esclusa",
                    "Tabelle ACI Superbollo 375 kW (510 CV): 190 kW eccedenti = 3.800€ solo di addizionale erariale",
                    "Prezzi fornitura pneumatici N-Rated (specifica Porsche)"
                ),
                videoGoal = "Generare alta retention e commenti polarizzati tra chi la considera un investimento e chi un pozzo senza fondo.",
                longVideoVersionSummary = "Analisi a capitoli: 1. Tasse e bollo statico; 2. Usura pista vs strada; 3. Tagliandi e garanzia Porsche Approved (costo e vincoli); 4. Il fattore svalutazione; 5. Tabella riassuntiva in Euro al km.",
                derivedShortsIdeas = listOf(
                    "Quanto dura un treno di gomme Cup 2 su una GT3 prima di dover spendere 1.800€?",
                    "Il costo folle del superbollo per una Porsche GT3 in Italia spiegato in 30 secondi."
                ),
                tiktokAdaptationNotes = "Focus dinamico sui numeri a schermo: sincronizzare i suoni di scarico con l'apparizione delle cifre di bollo.",
                instagramReelsAdaptationNotes = "Carosello video estetico con sound aspirato 9000 RPM e breakdown costi nel primo commento per massimizzare i salvataggi.",
                verifiedFacts = listOf(
                    "Potenza ufficiale 992 GT3: 510 CV (375 kW).",
                    "Addizionale erariale superbollo italiana: 20€ per ogni kW sopra i 185 kW.",
                    "Garanzia Porsche Approved annuale: costo medio ufficiale ~1.600€ - 1.900€ a seconda del modello."
                ),
                estimatedData = listOf(
                    "Consumo medio reale combinato: 14-16 litri/100km (stima da test drive di settore).",
                    "Premio Kasko completa con localizzatore: stima tra 3.500€ e 5.000€ in base alla classe di merito.",
                    "Durata pastiglie e dischi in acciaio: stima 25.000 km stradali o 6 sessioni pista."
                ),
                editorialHypotheses = listOf(
                    "La tenuta del valore sul mercato dell'usato potrebbe compensare parzialmente i costi vivi se l'inflazione dei modelli aspirati rimane alta."
                ),
                difficultyScore = 2,
                viralityPotential = "Molto Alto"
            ),
            AiStrategyProposal(
                id = "prop_2_supercar",
                category = AutomotiveCategory.SUPERCAR_AND_HYPERCAR,
                provisionalTitle = "Ferrari F80 vs McLaren W1: La Nuova Guerra dei 1200 Cavalli",
                coreIdea = "Confronto tecnico ed estetico tra i due manifesti tecnologici del 2024: V6 biturbo ibrido derivato dalla 499P di Le Mans contro il V8 biturbo ibrido di Woking.",
                potentialAudience = "Fanboy Ferrari e McLaren, amanti della Formula 1, ingegneri e appassionati di aerodinamica attiva.",
                initialHook = "Tre milioni e mezzo di euro per ciascuna. Più di 1200 cavalli. Ma mentre McLaren ha tenuto il V8, Ferrari ha scelto un V6. Chi ha avuto davvero il coraggio di osare?",
                recommendedFormat = ProjectFormat.LONG_VIDEO,
                suggestedDuration = "18 - 20 minuti",
                sourcesToVerify = listOf(
                    "Cartella stampa ufficiale Ferrari F80 (Maranello)",
                    "Dati tecnici ufficiali McLaren W1 (Woking)",
                    "Regolamento FIA WEC Hypercar per dettagli MGU-K ed e-turbo",
                    "Brevetti Ferrari sulle sospensioni attive con attuatori a 48V"
                ),
                videoGoal = "Posizionare LuxDrive come canale di riferimento tecnico autorevole in Italia per le hypercar di ultima generazione.",
                longVideoVersionSummary = "Struttura a round: Round 1 Powertrain (V6 vs V8 ibrido); Round 2 Aerodinamica e carico a 250 km/h; Round 3 Peso reale a secco vs ordini di marcia; Round 4 Design e cockpit.",
                derivedShortsIdeas = listOf(
                    "Perché la nuova Ferrari da 3,6 milioni ha 'solo' 6 cilindri? La verità tecnica di Le Mans.",
                    "Le portiere ad ali di gabbiano della McLaren W1: ingegneria o solo scena?"
                ),
                tiktokAdaptationNotes = "Clip a confronto visivo con transizioni a specchio e sondaggio: V6 Le Mans o V8 puro?",
                instagramReelsAdaptationNotes = "Highlight audio del sound banco prova dei due motori e render dei flussi aerodinamici sotto scocca.",
                verifiedFacts = listOf(
                    "Ferrari F80: 1200 CV totali (900 CV termici dal 3.0L V6 + 300 CV elettrici).",
                    "McLaren W1: 1275 CV totali dal 4.0L V8 biturbo con trazione solo posteriore.",
                    "Prezzo stimato base: F80 ~3,6 milioni € tasse escluse; W1 ~2,4 milioni €."
                ),
                estimatedData = listOf(
                    "Tempo sul giro a Fiorano: stimato sotto 1'16\" per la F80.",
                    "Peso reale in ordine di marcia con liquidi e pilota: stima tra 1550 e 1650 kg per entrambe."
                ),
                editorialHypotheses = listOf(
                    "La trazione integrale elettrificata della F80 potrebbe renderla più rapida sullo 0-200 km/h rispetto alla trazione posteriore pura di W1 su asfalto stradale."
                ),
                difficultyScore = 4,
                viralityPotential = "Altissimo"
            ),
            AiStrategyProposal(
                id = "prop_3_leggendarie",
                category = AutomotiveCategory.LEGENDARY_AND_RARE_CARS,
                provisionalTitle = "Lancia Delta S4 Stradale: Il Mostro Dimenticato da 1 Milione di Euro",
                coreIdea = "Approfondimento sulla rarissima versione omologata stradale del Gruppo B: volumetrico + turbo (Twincharging), interni in alcantara e un valore d'asta esploso.",
                potentialAudience = "Nostalgici del Rally Gruppo B, collezionisti d'epoca e cultori del design automobilistico italiano anni '80.",
                initialHook = "Ne hanno costruite solo 200 per poter correre nel Gruppo B. Oggi ciascuna vale oltre 1 milione di euro. Ma guidarla su strada nel 1985 era un incubo o un'estasi?",
                recommendedFormat = ProjectFormat.LONG_VIDEO,
                suggestedDuration = "14 minuti",
                sourcesToVerify = listOf(
                    "Risultati d'asta RM Sotheby's e Bonhams 2020-2024",
                    "Archivio Storico Abarth e progetti ing. Claudio Lombardi",
                    "Specifiche del compressore volumetrico Volumex accoppiato alla turbina KKK K27"
                ),
                videoGoal = "Valorizzare l'eccellenza e il dramma sportivo italiano, fidelizzando il pubblico senior.",
                longVideoVersionSummary = "Storia dello sviluppo Abarth 038, la complessità dell'erogazione della potenza, le differenze estetiche tra versione corsa e stradale, la quotazione moderna.",
                derivedShortsIdeas = listOf(
                    "Il motore che aveva sia il Turbo che il Volumetrico: come funzionava nel 1985?",
                    "Quanto vale oggi la Lancia Delta più pericolosa di sempre?"
                ),
                tiktokAdaptationNotes = "Suono autentico del compressore volumetrico che fischia ai bassi regimi, sottotitoli evidenziati in rosso.",
                instagramReelsAdaptationNotes = "Focus fotografico sui dettagli interni e sul telaio a traliccio tubolare a vista posteriore.",
                verifiedFacts = listOf(
                    "Motore 1.759 cc 4 cilindri in linea in posizione centrale.",
                    "Potenza versione stradale dichiarata: 250 CV; versione corsa: oltre 480 CV.",
                    "Telaio spaceframe in tubi d'acciaio al cromo-molibdeno."
                ),
                estimatedData = listOf(
                    "Esemplari superstiti certificati originali: stima di circa 80-110 vetture note nel mondo."
                ),
                editorialHypotheses = listOf(
                    "Se il Gruppo B non fosse stato cancellato dopo il tragico rogo del Tour de Corse 1986, la S4 avrebbe dominato la stagione successiva."
                ),
                difficultyScore = 3,
                viralityPotential = "Alto"
            ),
            AiStrategyProposal(
                id = "prop_4_retroscena",
                category = AutomotiveCategory.AUTOMOTIVE_STORIES_AND_BACKSTAGE,
                provisionalTitle = "Il Giorno in Cui Ferruccio Lamborghini Disse No a Enzo Ferrari",
                coreIdea = "La vera cronistoria della lite sulla frizione della 250 GT che portò alla nascita della Automobili Lamborghini a Sant'Agata Bolognese.",
                potentialAudience = "Tutti gli appassionati di auto, imprenditoria e leggende italiane della Motor Valley.",
                initialHook = "Se Enzo Ferrari non avesse offeso un ricco costruttore di trattori dicendogli 'Pensa alle tue macchine agricole', oggi la Miura e la Countach non esisterebbero.",
                recommendedFormat = ProjectFormat.LONG_VIDEO,
                suggestedDuration = "15 minuti",
                sourcesToVerify = listOf(
                    "Intervista storica registrata di Ferruccio Lamborghini a Tonino Lamborghini",
                    "Documenti di fornitura frizioni Borg & Beck identiche tra trattori e Ferrari",
                    "Disegni originali del motore V12 di Giotto Bizzarrini"
                ),
                videoGoal = "Storytelling immersivo per creare affezione al canale e massimizzare il watch time medio.",
                longVideoVersionSummary = "Dalla passione di Ferruccio per le Ferrari al difetto cronico della frizione; il colloquio a Maranello; il reclutamento dei geni dissidenti di Ferrari (Dallara, Stanzani, Bizzarrini); la presentazione della 350 GTV a Torino.",
                derivedShortsIdeas = listOf(
                    "La frizione che costava 10 volte di più solo perché aveva il cavallino rampante sopra!",
                    "Il capolavoro di Bizzarrini: un V12 progettato per vendetta contro Maranello."
                ),
                tiktokAdaptationNotes = "Ricostruzione a dialogo ritmato con immagini storiche in bianco e nero animate.",
                instagramReelsAdaptationNotes = "Citazione celebre di Ferruccio in copertina e soundtrack epica in stile Motor Valley.",
                verifiedFacts = listOf(
                    "Ferruccio Lamborghini possedeva diverse Ferrari tra cui una 250 GT.",
                    "Giotto Bizzarrini ricevette un compenso aggiuntivo per ogni cavallo erogato oltre il V12 Ferrari.",
                    "La fabbrica di Sant'Agata fu costruita in tempi record nel 1963."
                ),
                estimatedData = listOf(
                    "Costo del ricambio frizione commerciale: circa 1.000 lire contro le 10.000 fatturate a Maranello (stima da memorie di Ferruccio)."
                ),
                editorialHypotheses = listOf(
                    "La rivalità personale ha accelerato l'innovazione automobilistica europea di almeno 10 anni."
                ),
                difficultyScore = 2,
                viralityPotential = "Molto Alto"
            ),
            AiStrategyProposal(
                id = "prop_5_tecnologia",
                category = AutomotiveCategory.ENGINES_AND_TECHNOLOGY,
                provisionalTitle = "Come Funziona Davvero il Cambio a Doppia Frizione (PDK): Perché Non Sbaglia Mai?",
                coreIdea = "Spiegazione ingegneristica visuale ma comprensibile del principio a due alberi coassiali, preselezione della marcia e gestione delle frizioni a bagno d'olio.",
                potentialAudience = "Appassionati di tecnica, guidatori sportivi, curiosi del funzionamento meccanico.",
                initialHook = "Mentre stai accelerando in terza marcia, il cambio ha già ingranato la quarta e sta solo aspettando il millisecondo giusto per scambiare le frizioni. Ecco perché il PDK Porsche ha sconfitto i piloti professionisti.",
                recommendedFormat = ProjectFormat.LONG_VIDEO,
                suggestedDuration = "11 minuti",
                sourcesToVerify = listOf(
                    "Brevetti originali Porsche Doppelkupplung per la 962 di Le Mans negli anni '80",
                    "Manuali di riparazione e schemi idraulici ZF / Getrag",
                    "Dati di telemetria sui tempi di cambiata (sotto i 100 millisecondi)"
                ),
                videoGoal = "Costruire la reputazione di LuxDrive per l'analisi tecnica rigorosa con grafiche chiare.",
                longVideoVersionSummary = "Origine racing nel Gruppo C; alberi concentrici e marce pari/dispari; centralina meccatronica e tempi di incrocio; confronto con cambi sequenziali e convertitore di coppia moderno.",
                derivedShortsIdeas = listOf(
                    "Come fa il PDK a cambiare marcia in meno di un battito di ciglia?",
                    "Perché non dovresti MAI tenere premuto il freno al semaforo con una doppia frizione a secco."
                ),
                tiktokAdaptationNotes = "Animazione dello scambio tra albero primario e secondario a ritmo di beat techno.",
                instagramReelsAdaptationNotes = "Diagramma grafico pulito con frecce d'oro sui flussi di coppia.",
                verifiedFacts = listOf(
                    "Il PDK fu testato in gara per la prima volta sulla Porsche 956/962.",
                    "Il tempo di interruzione di coppia è virtualmente nullo.",
                    "Le marce pari e dispari risiedono su due alberi di trasmissione fisicamente distinti."
                ),
                estimatedData = listOf(
                    "Velocità attuazione idraulica moderna: tra 40 e 80 ms nelle modalità Sport Plus."
                ),
                editorialHypotheses = listOf(
                    "Nonostante il perfezionamento dei cambi automatici con convertitore a 8 o 9 rapporti, la doppia frizione rimane insuperabile per feedback e reattività in pista."
                ),
                difficultyScore = 3,
                viralityPotential = "Medio-Alto"
            ),
            AiStrategyProposal(
                id = "prop_6_confronti",
                category = AutomotiveCategory.PERFORMANCE_AND_COMPARISONS,
                provisionalTitle = "BMW M3 Touring vs Audi RS4 Avant: La Battaglia Definitiva delle Familiari da Pista",
                coreIdea = "Confronto impietoso su handling, trazione integrale xDrive disinseribile vs Quattro classico, sound motore e abitabilità reale.",
                potentialAudience = "Acquirenti del segmento premium sportivo, padri di famiglia appassionati di cavalli e track day.",
                initialHook = "Oltre 500 cavalli, baule per i bagagli delle vacanze e prestazioni da supercar di 5 anni fa. BMW ha finalmente costruito la M3 Touring, ma l'Audi RS4 cede davvero lo scettro?",
                recommendedFormat = ProjectFormat.LONG_VIDEO,
                suggestedDuration = "16 minuti",
                sourcesToVerify = listOf(
                    "Tempi ufficiali certificati al Nürburgring Nordschleife",
                    "Rilevamenti peso su bilance a quattro piatti di riviste indipendenti",
                    "Scheda tecnica S58 BMW 3.0L twin-turbo vs 2.9L V6 biturbo Audi/Porsche"
                ),
                videoGoal = "Generare discussione accesa e commenti di tifoseria tra appassionati BMW e Audi.",
                longVideoVersionSummary = "Estetica e grinta; Dinamica telaio e comportamento dell'avantreno; Frenata e resistenza al fading; Spese di gestione annuali a confronto; Conclusione definitiva per chi cerca il tempo sul giro o la daily driver perfetta.",
                derivedShortsIdeas = listOf(
                    "Sound check a confronto: 6 cilindri in linea BMW vs V6 Audi RS!",
                    "Il tempo al Nürburgring della M3 Touring che ha sconvolto il segmento."
                ),
                tiktokAdaptationNotes = "Split screen con drag race e audio accelerazione da fermo.",
                instagramReelsAdaptationNotes = "Foto comparison carosello con tabella pesi e potenze evidenziata.",
                verifiedFacts = listOf(
                    "M3 Touring Competition: 510 CV (ora 530 CV nel restyling), motore S58.",
                    "RS4 Avant Competition: 450 CV, motore 2.9L V6 biturbo.",
                    "Tempo M3 Touring al Nordschleife: 7'35\"060 (record per station wagon di serie)."
                ),
                estimatedData = listOf(
                    "Peso effettivo con pieno: circa 1.865 kg per BMW, circa 1.810 kg per Audi."
                ),
                editorialHypotheses = listOf(
                    "L'avantreno più affilato e la trazione posteriore pura opzionale rendono la M3 più adrenalinica per chi frequenta la pista."
                ),
                difficultyScore = 2,
                viralityPotential = "Molto Alto"
            )
        )
    }
}
