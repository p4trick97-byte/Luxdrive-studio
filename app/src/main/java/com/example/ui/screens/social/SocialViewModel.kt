package com.example.ui.screens.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.VideoProject
import com.example.data.repository.VideoProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SocialUiState(
    val projectsWithSocial: List<VideoProject> = emptyList(),
    val selectedPlatformTab: String = "TIKTOK", // "TIKTOK", "INSTAGRAM", "SHORTS"
    val feedbackMessage: String? = null
)

class SocialViewModel(
    private val projectRepository: VideoProjectRepository
) : ViewModel() {

    private val _selectedPlatformTab = MutableStateFlow("TIKTOK")
    private val _feedbackMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SocialUiState> = combine(
        projectRepository.allProjects,
        _selectedPlatformTab,
        _feedbackMessage
    ) { projects, tab, msg ->
        val withSocial = projects.filter {
            it.tiktokAdaptation.isNotBlank() ||
            it.instagramReelAdaptation.isNotBlank() ||
            it.shortsDerivedScript.isNotBlank()
        }
        SocialUiState(
            projectsWithSocial = withSocial,
            selectedPlatformTab = tab,
            feedbackMessage = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SocialUiState()
    )

    fun selectPlatformTab(tab: String) {
        _selectedPlatformTab.value = tab
    }

    fun setFeedback(msg: String) {
        _feedbackMessage.value = msg
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }
}
