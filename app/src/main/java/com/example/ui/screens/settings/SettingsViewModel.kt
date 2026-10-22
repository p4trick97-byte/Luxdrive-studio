package com.example.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AnalyticsRepository
import com.example.data.repository.IntegrationBridgeManager
import com.example.data.repository.VideoProjectRepository
import com.example.data.repository.YouTubeApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val channelId: String = "UC_LUXDRIVE_ITALIA",
    val requiredSubscribers: Int = 1000,
    val requiredWatchHours: Double = 4000.0,
    val requiredShortsViews: Long = 10_000_000L,
    val youtubeConfig: YouTubeApiConfig = YouTubeApiConfig(),
    val isSyncing: Boolean = false,
    val syncIntervalHours: Int = 12,
    val feedbackMessage: String? = null
)

class SettingsViewModel(
    private val analyticsRepository: AnalyticsRepository,
    private val projectRepository: VideoProjectRepository,
    private val integrationBridge: IntegrationBridgeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            integrationBridge.youtubeConfig.collect { yt ->
                _uiState.value = _uiState.value.copy(youtubeConfig = yt)
            }
        }

        viewModelScope.launch {
            integrationBridge.isSyncing.collect { syncing ->
                _uiState.value = _uiState.value.copy(isSyncing = syncing)
            }
        }
    }

    fun updateChannelId(newId: String) {
        _uiState.value = _uiState.value.copy(channelId = newId)
    }

    fun updateMonetizationThresholds(subs: Int, hours: Double, shorts: Long) {
        _uiState.value = _uiState.value.copy(
            requiredSubscribers = subs,
            requiredWatchHours = hours,
            requiredShortsViews = shorts,
            feedbackMessage = "Soglie di monetizzazione YPP aggiornate!"
        )
    }

    fun setSyncIntervalHours(hours: Int) {
        _uiState.value = _uiState.value.copy(
            syncIntervalHours = hours,
            feedbackMessage = "Frequenza di verifica impostata a ogni $hours ore."
        )
    }

    fun testApiConnection() {
        viewModelScope.launch {
            val res = integrationBridge.simulateSyncCheck()
            _uiState.value = _uiState.value.copy(feedbackMessage = res.getOrNull())
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            analyticsRepository.clearHistory()
            analyticsRepository.seedInitialMetricsIfEmpty()
            projectRepository.seedInitialProjectsIfEmpty()
            _uiState.value = _uiState.value.copy(feedbackMessage = "Dati dimostrativi ripristinati correttamente.")
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}
