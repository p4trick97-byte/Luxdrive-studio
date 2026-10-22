package com.example.ui.screens.strategy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiStrategyProposal
import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import com.example.data.repository.AiStrategyRepository
import com.example.data.repository.AnalyticsRepository
import com.example.data.repository.VideoProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StrategyUiState(
    val proposals: List<AiStrategyProposal> = emptyList(),
    val selectedCategory: AutomotiveCategory? = null,
    val selectedProposal: AiStrategyProposal? = null,
    val isGenerating: Boolean = false,
    val feedbackMessage: String? = null,
    val importedProjectId: Long? = null
)

class AiStrategyViewModel(
    private val strategyRepository: AiStrategyRepository,
    private val projectRepository: VideoProjectRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StrategyUiState())
    val uiState: StateFlow<StrategyUiState> = _uiState.asStateFlow()

    private var allProposalsList: List<AiStrategyProposal> = emptyList()

    init {
        viewModelScope.launch {
            strategyRepository.proposals.collect { list ->
                allProposalsList = list
                updateFilter()
            }
        }
    }

    private fun updateFilter() {
        val cat = _uiState.value.selectedCategory
        val filtered = if (cat == null) {
            allProposalsList
        } else {
            allProposalsList.filter { it.category == cat }
        }
        val currentSel = _uiState.value.selectedProposal
        val newSel = if (filtered.any { it.id == currentSel?.id }) currentSel else filtered.firstOrNull()

        _uiState.value = _uiState.value.copy(
            proposals = filtered,
            selectedProposal = newSel
        )
    }

    fun selectCategory(category: AutomotiveCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        updateFilter()
    }

    fun selectProposal(proposal: AiStrategyProposal) {
        _uiState.value = _uiState.value.copy(selectedProposal = proposal)
    }

    fun generateStrategicProposals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true)
            kotlinx.coroutines.delay(1000)
            strategyRepository.generateNewProposalsForChannel(
                mostViewedCategory = "Quanto costa davvero mantenere una supercar",
                channelRetentionRate = 51.2
            )
            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                feedbackMessage = "Algoritmo strategico aggiornato con i trend di retention del canale!"
            )
        }
    }

    fun importProposalToVideoLab(proposal: AiStrategyProposal) {
        viewModelScope.launch {
            val project = VideoProject(
                title = proposal.provisionalTitle,
                category = proposal.category.displayName,
                format = proposal.recommendedFormat.name,
                status = ProjectStatus.DRAFT.name,
                targetAudience = proposal.potentialAudience,
                hook = proposal.initialHook,
                researchAndSources = proposal.sourcesToVerify.joinToString("\n- ", prefix = "- "),
                fullScript = "BOZZA SCRIPT BASATA SU STRATEGIA LUXDRIVE:\n\n${proposal.longVideoVersionSummary}\n\n[Compila qui lo script completo]",
                voiceOverNotes = "Obiettivo: ${proposal.videoGoal}\nDurata target: ${proposal.suggestedDuration}",
                capcutEditingNotes = "Linee guida derivate: ${proposal.tiktokAdaptationNotes}",
                youtubeTitleAlternative = proposal.provisionalTitle,
                description = "${proposal.coreIdea}\n\nIscriviti a LuxDrive Italia per altri contenuti esclusivi!",
                tags = "luxdrive, supercar, automotive, ${proposal.category.displayName.lowercase()}",
                thumbnailConcept = "Idea miniatura ad alto contrasto per: ${proposal.provisionalTitle}",
                shortsDerivedScript = proposal.derivedShortsIdeas.firstOrNull() ?: "",
                tiktokAdaptation = proposal.tiktokAdaptationNotes,
                instagramReelAdaptation = proposal.instagramReelsAdaptationNotes
            )
            val newId = projectRepository.saveProject(project)
            _uiState.value = _uiState.value.copy(
                importedProjectId = newId,
                feedbackMessage = "Proposta trasferita con successo nel Video Lab!"
            )
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(
            feedbackMessage = null,
            importedProjectId = null
        )
    }
}
