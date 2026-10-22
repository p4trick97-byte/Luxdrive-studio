package com.example.ui.screens.videolab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import com.example.data.repository.VideoProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VideoLabUiState(
    val projects: List<VideoProject> = emptyList(),
    val currentEditingProject: VideoProject? = null,
    val selectedStatusFilter: String? = null,
    val selectedFormatFilter: String? = null,
    val searchQuery: String = "",
    val feedbackMessage: String? = null,
    val newlyCreatedProjectId: Long? = null,
    val isSaving: Boolean = false
)

class VideoLabViewModel(
    private val projectRepository: VideoProjectRepository
) : ViewModel() {

    private val _projects = MutableStateFlow<List<VideoProject>>(emptyList())
    private val _currentEditingProject = MutableStateFlow<VideoProject?>(null)
    private val _selectedStatusFilter = MutableStateFlow<String?>(null)
    private val _selectedFormatFilter = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    private val _newlyCreatedProjectId = MutableStateFlow<Long?>(null)
    private val _isSaving = MutableStateFlow(false)

    val uiState: StateFlow<VideoLabUiState> = projectRepository.allProjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    ).let { repoFlow ->
        MutableStateFlow(VideoLabUiState()).apply {
            viewModelScope.launch {
                repoFlow.collect { list ->
                    _projects.value = list
                }
            }
        }
    }

    private val _state = MutableStateFlow(VideoLabUiState())
    val state: StateFlow<VideoLabUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            projectRepository.allProjects.collect { list ->
                updateFilteredList(list, _selectedStatusFilter.value, _selectedFormatFilter.value, _searchQuery.value)
            }
        }
    }

    fun setStatusFilter(status: String?) {
        _selectedStatusFilter.value = status
        viewModelScope.launch {
            projectRepository.allProjects.collect { list ->
                updateFilteredList(list, status, _selectedFormatFilter.value, _searchQuery.value)
            }
        }
    }

    fun setFormatFilter(format: String?) {
        _selectedFormatFilter.value = format
        viewModelScope.launch {
            projectRepository.allProjects.collect { list ->
                updateFilteredList(list, _selectedStatusFilter.value, format, _searchQuery.value)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            projectRepository.allProjects.collect { list ->
                updateFilteredList(list, _selectedStatusFilter.value, _selectedFormatFilter.value, query)
            }
        }
    }

    private fun updateFilteredList(
        all: List<VideoProject>,
        status: String?,
        format: String?,
        query: String
    ) {
        var filtered = all
        if (!status.isNullOrBlank()) {
            filtered = filtered.filter { it.status == status }
        }
        if (!format.isNullOrBlank()) {
            filtered = filtered.filter { it.format == format }
        }
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.tags.contains(query, ignoreCase = true)
            }
        }
        _state.value = _state.value.copy(
            projects = filtered,
            selectedStatusFilter = status,
            selectedFormatFilter = format,
            searchQuery = query
        )
    }

    fun loadProject(id: Long) {
        viewModelScope.launch {
            val p = projectRepository.getProjectById(id)
            _state.value = _state.value.copy(currentEditingProject = p)
        }
    }

    fun createNewProject(format: ProjectFormat = ProjectFormat.LONG_VIDEO) {
        viewModelScope.launch {
            val defaultCapCut = """
FASE 1: IMPORT & PROPORZIONI
1. Apri CapCut su smartphone > Nuovo Progetto.
2. Imposta Formato: ${if (format == ProjectFormat.SHORTS) "9:16 (Verticale)" else "16:9 (Orizzontale)"}.
3. Risoluzione: 1080p a 60fps (o 4K a 30fps).

FASE 2: MONTAGGIO RITMATO
- Taglia ogni pausa superiore a 0.3 secondi nella voce narrante.
- Inserisci b-roll dell'auto ogni 3-4 secondi max per non perdere l'attenzione.
- Zoom lento continuo ("Keyframe scale 100% -> 110%") sulle inquadrature statiche di motore e fari.

FASE 3: TESTO & GRAFICA
- Aggiungi testo 'Montserrat / The Bold Font' colore Giallo Oro (#FFDF00) per le cifre in euro e cavalli.
- Effetto sonoro 'Swoosh' o 'Thud' sulle apparizioni delle tabelle costi.

FASE 4: AUDIO & COLOR GRADING
- Regola volume musica di sottofondo a -22 dB rispetto alla traccia voce (0 dB).
- Filtro colore consigliato: 'Nero & Oro / Cinematic' (+10 contrasto, -5 saturazione su sfondi, +15 nitidezza).
            """.trimIndent()

            val p = VideoProject(
                title = "Nuovo Progetto Video Automotive",
                category = AutomotiveCategory.SUPERCAR_MAINTENANCE_REAL_COST.displayName,
                format = format.name,
                status = ProjectStatus.DRAFT.name,
                hook = "Ecco cosa succede quando decidi di comprare una...",
                capcutEditingNotes = defaultCapCut,
                fullScript = "INTRO:\n[Scrivi qui l'apertura del video]\n\nPARTE 1: ANALISI TECNICA:\n...\n\nCONCLUSIONI:\n...",
                chaptersText = "0:00 Introduzione\n1:30 Primo blocco\n5:00 Dati e cifre\n9:00 Verdetto",
                tags = "luxdrive, automotive, motori, auto sportive",
                thumbnailConcept = "Inquadratura 3/4 frontale dell'auto con testo gigante in oro lucido."
            )
            val newId = projectRepository.saveProject(p)
            _state.value = _state.value.copy(
                newlyCreatedProjectId = newId,
                feedbackMessage = "Nuovo progetto creato!"
            )
        }
    }

    fun saveProject(project: VideoProject) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            projectRepository.saveProject(project)
            _state.value = _state.value.copy(
                isSaving = false,
                currentEditingProject = project,
                feedbackMessage = "Progetto salvato con successo nell'archivio!"
            )
        }
    }

    fun duplicateProject(id: Long) {
        viewModelScope.launch {
            val copyId = projectRepository.duplicateProject(id)
            if (copyId != null) {
                _state.value = _state.value.copy(
                    newlyCreatedProjectId = copyId,
                    feedbackMessage = "Progetto duplicato con successo!"
                )
            }
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            projectRepository.deleteProject(id)
            _state.value = _state.value.copy(
                currentEditingProject = null,
                feedbackMessage = "Progetto eliminato dall'archivio."
            )
        }
    }

    fun clearFeedback() {
        _state.value = _state.value.copy(
            feedbackMessage = null,
            newlyCreatedProjectId = null
        )
    }
}
