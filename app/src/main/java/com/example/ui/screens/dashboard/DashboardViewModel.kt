package com.example.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChannelMetric
import com.example.data.model.MonetizationStatus
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import com.example.data.repository.AnalyticsRepository
import com.example.data.repository.IntegrationBridgeManager
import com.example.data.repository.VideoProjectRepository
import com.example.data.repository.YouTubeApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val latestMetric: ChannelMetric? = null,
    val monetizationStatus: MonetizationStatus = MonetizationStatus(),
    val publishedCount: Int = 0,
    val inProgressCount: Int = 0,
    val recentProjects: List<VideoProject> = emptyList(),
    val nextScheduledProjects: List<VideoProject> = emptyList(),
    val youtubeConfig: YouTubeApiConfig = YouTubeApiConfig(),
    val isSyncing: Boolean = false,
    val snackbarMessage: String? = null
)

class DashboardViewModel(
    private val projectRepository: VideoProjectRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val integrationBridge: IntegrationBridgeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            projectRepository.seedInitialProjectsIfEmpty()
            analyticsRepository.seedInitialMetricsIfEmpty()
        }

        viewModelScope.launch {
            projectRepository.allProjects.collect { projects ->
                val published = projects.filter { it.status == ProjectStatus.PUBLISHED.name }
                val inProgress = projects.filter { it.status != ProjectStatus.PUBLISHED.name }
                val scheduled = projects
                    .filter { it.scheduledDateMillis != null && it.status != ProjectStatus.PUBLISHED.name }
                    .sortedBy { it.scheduledDateMillis }

                _uiState.value = _uiState.value.copy(
                    publishedCount = published.size,
                    inProgressCount = inProgress.size,
                    recentProjects = projects.take(5),
                    nextScheduledProjects = scheduled.take(3)
                )
            }
        }

        viewModelScope.launch {
            analyticsRepository.latestMetric.collect { metric ->
                _uiState.value = _uiState.value.copy(latestMetric = metric)
            }
        }

        viewModelScope.launch {
            analyticsRepository.monetizationStatus.collect { mon ->
                _uiState.value = _uiState.value.copy(monetizationStatus = mon)
            }
        }

        viewModelScope.launch {
            integrationBridge.youtubeConfig.collect { yt ->
                _uiState.value = _uiState.value.copy(youtubeConfig = yt)
            }
        }

        viewModelScope.launch {
            integrationBridge.isSyncing.collect { sync ->
                _uiState.value = _uiState.value.copy(isSyncing = sync)
            }
        }
    }

    fun triggerSync() {
        viewModelScope.launch {
            val result = integrationBridge.simulateSyncCheck()
            _uiState.value = _uiState.value.copy(
                snackbarMessage = result.getOrNull() ?: "Errore di sincronizzazione"
            )
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
