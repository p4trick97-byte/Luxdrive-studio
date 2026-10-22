package com.example.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChannelMetric
import com.example.data.model.VideoProject
import com.example.data.repository.AnalyticsRepository
import com.example.data.repository.IntegrationBridgeManager
import com.example.data.repository.VideoProjectRepository
import com.example.data.repository.YouTubeApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PeriodFilter(val label: String, val days: Int) {
    DAYS_7("7 Giorni", 7),
    DAYS_28("28 Giorni", 28),
    DAYS_90("90 Giorni", 90),
    ALL_TIME("Tutto", 365)
}

data class AnalyticsUiState(
    val latestMetric: ChannelMetric? = null,
    val metricsHistory: List<ChannelMetric> = emptyList(),
    val filteredMetrics: List<ChannelMetric> = emptyList(),
    val publishedVideos: List<VideoProject> = emptyList(),
    val selectedPeriod: PeriodFilter = PeriodFilter.DAYS_28,
    val selectedFormatTab: String = "TUTTI", // "TUTTI", "LONG", "SHORTS"
    val comparisonVideoA: VideoProject? = null,
    val comparisonVideoB: VideoProject? = null,
    val youtubeConfig: YouTubeApiConfig = YouTubeApiConfig(),
    val isSyncing: Boolean = false,
    val showAddMetricModal: Boolean = false,
    val feedbackMessage: String? = null
)

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository,
    private val projectRepository: VideoProjectRepository,
    private val integrationBridge: IntegrationBridgeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    private var allMetricsList: List<ChannelMetric> = emptyList()
    private var allPublishedList: List<VideoProject> = emptyList()

    init {
        viewModelScope.launch {
            analyticsRepository.latestMetric.collect { latest ->
                _uiState.value = _uiState.value.copy(latestMetric = latest)
            }
        }

        viewModelScope.launch {
            analyticsRepository.allMetrics.collect { list ->
                allMetricsList = list
                applyFilters()
            }
        }

        viewModelScope.launch {
            projectRepository.allProjects.collect { projects ->
                val published = projects.filter { it.publishedDateMillis != null || it.viewsCount > 0 }
                allPublishedList = published
                val currentA = _uiState.value.comparisonVideoA
                val currentB = _uiState.value.comparisonVideoB
                val compA = published.firstOrNull { it.id == currentA?.id } ?: published.firstOrNull()
                val compB = published.firstOrNull { it.id == currentB?.id } ?: published.getOrNull(1)

                _uiState.value = _uiState.value.copy(
                    publishedVideos = published,
                    comparisonVideoA = compA,
                    comparisonVideoB = compB
                )
            }
        }

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

    private fun applyFilters() {
        val period = _uiState.value.selectedPeriod
        val cutoff = System.currentTimeMillis() - (period.days.toLong() * 86_400_000L)
        val filtered = if (period == PeriodFilter.ALL_TIME) {
            allMetricsList
        } else {
            allMetricsList.filter { it.recordDateMillis >= cutoff }
        }
        _uiState.value = _uiState.value.copy(
            metricsHistory = allMetricsList,
            filteredMetrics = filtered
        )
    }

    fun selectPeriod(period: PeriodFilter) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        applyFilters()
    }

    fun selectFormatTab(format: String) {
        _uiState.value = _uiState.value.copy(selectedFormatTab = format)
    }

    fun setComparisonVideos(aId: Long?, bId: Long?) {
        val compA = allPublishedList.firstOrNull { it.id == aId }
        val compB = allPublishedList.firstOrNull { it.id == bId }
        _uiState.value = _uiState.value.copy(
            comparisonVideoA = compA,
            comparisonVideoB = compB
        )
    }

    fun setShowAddMetricModal(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddMetricModal = show)
    }

    fun triggerSync() {
        viewModelScope.launch {
            val res = integrationBridge.simulateSyncCheck()
            _uiState.value = _uiState.value.copy(feedbackMessage = res.getOrNull())
        }
    }

    fun addManualMetricRecord(
        views: Long,
        subscribers: Int,
        watchHours: Double,
        impressions: Long,
        ctr: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val metric = ChannelMetric(
                recordDateMillis = System.currentTimeMillis(),
                totalSubscribers = subscribers,
                totalWatchHours = watchHours,
                totalViews = views,
                impressions = impressions,
                ctrPercent = ctr,
                isDemoData = false,
                dataSource = "MANUAL_ENTRY",
                notes = notes
            )
            analyticsRepository.recordMetric(metric)
            _uiState.value = _uiState.value.copy(
                showAddMetricModal = false,
                feedbackMessage = "Rilevazione manuale registrata nell'archivio!"
            )
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}
