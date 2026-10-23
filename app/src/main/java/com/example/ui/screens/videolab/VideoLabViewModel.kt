package com.example.ui.screens.videolab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat
import com.example.data.model.ProjectOrigin
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import com.example.data.repository.VideoProjectRepository
import com.example.data.service.AiBackendStatus
import com.example.data.service.AiScriptRequest
import com.example.data.service.GeminiAutomotiveAiClient
import com.example.data.service.GeneratedAutomotiveScript
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VideoLabUiState(
    val projects: List<VideoProject> = emptyList(),
    val currentEditingProject: VideoProject? = null,
    val selectedStatusFilter: String? = null,
    val selectedFormatFilter: String? = null,
    val searchQuery: String = "",
    val feedbackMessage: String? = null,
    val newlyCreatedProjectId: Long? = null,
    val isSaving: Boolean = false,
    // AI Generation states
    val isGeneratingAi: Boolean = false,
    val aiGenerationProgress: String? = null,
    val aiGenerationError: String? = null,
    val showAiGeneratorDialog: Boolean = false,
    val aiBackendStatus: AiBackendStatus = AiBackendStatus.NotConfigured("Inizializzazione in corso..."),
    val lastAiRequest: AiScriptRequest? = null
)

class VideoLabViewModel(
    private val projectRepository: VideoProjectRepository,
    private val aiClient: GeminiAutomotiveAiClient
) : ViewModel() {

    private val _state = MutableStateFlow(VideoLabUiState())
    val state: StateFlow<VideoLabUiState> = _state.asStateFlow()

    private var allProjectsCache: List<VideoProject> = emptyList()

    init {
        checkBackendStatus()
        viewModelScope.launch {
            projectRepository.allProjects.collect { list ->
                allProjectsCache = list
                updateFilteredList(list, _state.value.selectedStatusFilter, _state.value.selectedFormatFilter, _state.value.searchQuery)
            }
        }
    }

    fun checkBackendStatus() {
        val status = aiClient.getBackendStatus()
        _state.value = _state.value.copy(aiBackendStatus = status)
    }

    fun setShowAiGeneratorDialog(show: Boolean) {
        _state.value = _state.value.copy(
            showAiGeneratorDialog = show,
            aiGenerationError = if (show) null else _state.value.aiGenerationError
        )
    }

    fun setStatusFilter(status: String?) {
        _state.value = _state.value.copy(selectedStatusFilter = status)
        updateFilteredList(allProjectsCache, status, _state.value.selectedFormatFilter, _state.value.searchQuery)
    }

    fun setFormatFilter(format: String?) {
        _state.value = _state.value.copy(selectedFormatFilter = format)
        updateFilteredList(allProjectsCache, _state.value.selectedStatusFilter, format, _state.value.searchQuery)
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        updateFilteredList(allProjectsCache, _state.value.selectedStatusFilter, _state.value.selectedFormatFilter, query)
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
                it.tags.contains(query, ignoreCase = true) ||
                it.carModel.contains(query, ignoreCase = true)
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

    fun generateScriptWithAi(
        request: AiScriptRequest,
        targetExistingProjectId: Long? = null
    ) {
        if (_state.value.isGeneratingAi) return // Prevent duplicate clicks

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isGeneratingAi = true,
                aiGenerationProgress = "Connessione a Gemini 3.8 Flash...",
                aiGenerationError = null,
                lastAiRequest = request
            )

            val result = aiClient.generateScript(
                request = request,
                onProgress = { progressMessage ->
                    _state.value = _state.value.copy(aiGenerationProgress = progressMessage)
                }
            )
            result.fold(
                onSuccess = { generatedScript ->
                    val projectToSave = if (targetExistingProjectId != null && targetExistingProjectId > 0) {
                        val existing = projectRepository.getProjectById(targetExistingProjectId)
                        existing?.copy(
                            title = generatedScript.title,
                            category = request.category.displayName,
                            format = request.format.name,
                            hook = generatedScript.hook,
                            fullScript = generatedScript.fullScript,
                            voiceOverNotes = generatedScript.voiceOverNotes,
                            researchAndSources = generatedScript.researchAndSources,
                            storyboardJson = generatedScript.storyboardSummary,
                            capcutEditingNotes = generatedScript.capcutEditingNotes,
                            estimatedDurationMinutes = request.durationMinutes,
                            youtubeTitleAlternative = generatedScript.youtubeTitleAlternative,
                            description = generatedScript.description,
                            chaptersText = generatedScript.chaptersText,
                            tags = generatedScript.tags,
                            thumbnailConcept = generatedScript.thumbnailConcept,
                            shortsDerivedScript = generatedScript.shortsDerivedScript,
                            tiktokAdaptation = generatedScript.tiktokAdaptation,
                            instagramReelAdaptation = generatedScript.instagramReelAdaptation,
                            origin = ProjectOrigin.AI_GENERATED.name,
                            carModel = request.carTopic,
                            contentGoal = request.contentGoal,
                            narrativeTone = request.narrativeTone,
                            userNotes = request.userSourcesOrNotes,
                            updatedAtMillis = System.currentTimeMillis()
                        ) ?: buildNewProjectFromAi(request, generatedScript)
                    } else {
                        buildNewProjectFromAi(request, generatedScript)
                    }

                    val savedId = projectRepository.saveProject(projectToSave)
                    _state.value = _state.value.copy(
                        isGeneratingAi = false,
                        aiGenerationProgress = null,
                        showAiGeneratorDialog = false,
                        newlyCreatedProjectId = savedId,
                        currentEditingProject = projectToSave.copy(id = savedId),
                        feedbackMessage = "Script generato con successo dall'AI per: ${request.carTopic}"
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isGeneratingAi = false,
                        aiGenerationProgress = null,
                        aiGenerationError = error.localizedMessage ?: "Errore imprevisto durante la generazione."
                    )
                }
            )
        }
    }

    private fun buildNewProjectFromAi(request: AiScriptRequest, generated: GeneratedAutomotiveScript): VideoProject {
        return VideoProject(
            title = generated.title,
            category = request.category.displayName,
            format = request.format.name,
            status = ProjectStatus.DRAFT.name,
            hook = generated.hook,
            fullScript = generated.fullScript,
            voiceOverNotes = generated.voiceOverNotes,
            researchAndSources = generated.researchAndSources,
            storyboardJson = generated.storyboardSummary,
            capcutEditingNotes = generated.capcutEditingNotes,
            estimatedDurationMinutes = request.durationMinutes,
            youtubeTitleAlternative = generated.youtubeTitleAlternative,
            description = generated.description,
            chaptersText = generated.chaptersText,
            tags = generated.tags,
            thumbnailConcept = generated.thumbnailConcept,
            shortsDerivedScript = generated.shortsDerivedScript,
            tiktokAdaptation = generated.tiktokAdaptation,
            instagramReelAdaptation = generated.instagramReelAdaptation,
            origin = ProjectOrigin.AI_GENERATED.name,
            carModel = request.carTopic,
            contentGoal = request.contentGoal,
            narrativeTone = request.narrativeTone,
            userNotes = request.userSourcesOrNotes
        )
    }

    fun createNewManualProject(format: ProjectFormat = ProjectFormat.LONG_VIDEO) {
        viewModelScope.launch {
            val p = VideoProject(
                title = "Nuovo Progetto Manuale",
                category = AutomotiveCategory.SUPERCAR_MAINTENANCE_REAL_COST.displayName,
                format = format.name,
                status = ProjectStatus.DRAFT.name,
                origin = ProjectOrigin.USER_CREATED.name,
                hook = "",
                fullScript = "",
                capcutEditingNotes = "Linee guida montaggio smartphone CapCut...",
                chaptersText = "",
                tags = "luxdrive, automotive",
                thumbnailConcept = ""
            )
            val newId = projectRepository.saveProject(p)
            _state.value = _state.value.copy(
                newlyCreatedProjectId = newId,
                feedbackMessage = "Nuova bozza creata!"
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

    fun clearAiError() {
        _state.value = _state.value.copy(aiGenerationError = null)
    }
}
