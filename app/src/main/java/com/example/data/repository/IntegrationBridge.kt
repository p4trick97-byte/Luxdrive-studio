package com.example.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class IntegrationStatus(val label: String, val isConnected: Boolean) {
    NOT_CONFIGURED("Non configurato", false),
    AWAITING_OAUTH("In attesa di autorizzazione", false),
    CONNECTED("Collegato e attivo", true),
    TOKEN_EXPIRED("Token scaduto / da rinnovare", false),
    ERROR("Errore di connessione", false)
}

data class YouTubeApiConfig(
    val channelId: String = "",
    val apiKeyConfigured: Boolean = false,
    val oauthClientIdConfigured: Boolean = false,
    val status: IntegrationStatus = IntegrationStatus.NOT_CONFIGURED,
    val lastSyncTimestampMillis: Long? = null,
    val lastErrorMessage: String? = null
)

data class ExternalPlatformConfig(
    val platformName: String,
    val accountHandle: String = "",
    val status: IntegrationStatus = IntegrationStatus.NOT_CONFIGURED,
    val lastSyncTimestampMillis: Long? = null
)

interface YouTubeDataApiService {
    suspend fun getChannelProfile(channelId: String): Result<Map<String, Any>>
    suspend fun getUploadsPlaylistVideos(maxResults: Int): Result<List<Map<String, Any>>>
    suspend fun getVideoStatistics(videoId: String): Result<Map<String, Any>>
}

interface YouTubeAnalyticsApiService {
    suspend fun getChannelDailyReport(startDate: String, endDate: String): Result<List<Map<String, Any>>>
    suspend fun getAudienceRetentionReport(videoId: String): Result<Map<String, Any>>
    suspend fun getMonetizationEarningsReport(startDate: String, endDate: String): Result<Map<String, Any>>
}

interface GoogleOAuthManager {
    fun getAuthStatus(): Flow<IntegrationStatus>
    suspend fun initiateAuthorizationFlow(): Result<String>
    suspend fun disconnectAccount(): Result<Unit>
}

interface RemoteSyncService {
    fun isSyncInProgress(): Flow<Boolean>
    suspend fun triggerManualSync(): Result<Long>
    suspend fun configureScheduledIntervalHours(hours: Int)
}

interface GeminiAiAutomotiveService {
    suspend fun generateAutomotiveIdea(
        prompt: String,
        category: String,
        channelRetentionSummary: String
    ): Result<String>
}

interface SecondarySocialDistributionService {
    suspend fun stageTikTokDraft(title: String, script: String): Result<String>
    suspend fun stageInstagramReelDraft(title: String, caption: String): Result<String>
}

class IntegrationBridgeManager {

    private val _youtubeConfig = MutableStateFlow(
        YouTubeApiConfig(
            channelId = "UC_LUXDRIVE_ITALIA",
            apiKeyConfigured = false,
            oauthClientIdConfigured = false,
            status = IntegrationStatus.NOT_CONFIGURED,
            lastSyncTimestampMillis = null,
            lastErrorMessage = "Nessun client OAuth Google configurato. Le metriche attuali sono in modalità dimostrativa locale."
        )
    )
    val youtubeConfig: Flow<YouTubeApiConfig> = _youtubeConfig.asStateFlow()

    private val _tikTokConfig = MutableStateFlow(
        ExternalPlatformConfig(
            platformName = "TikTok",
            accountHandle = "@luxdrive_italia",
            status = IntegrationStatus.NOT_CONFIGURED
        )
    )
    val tikTokConfig: Flow<ExternalPlatformConfig> = _tikTokConfig.asStateFlow()

    private val _instagramConfig = MutableStateFlow(
        ExternalPlatformConfig(
            platformName = "Instagram Reels",
            accountHandle = "@luxdrive_magazine",
            status = IntegrationStatus.NOT_CONFIGURED
        )
    )
    val instagramConfig: Flow<ExternalPlatformConfig> = _instagramConfig.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: Flow<Boolean> = _isSyncing.asStateFlow()

    suspend fun simulateSyncCheck(): Result<String> {
        _isSyncing.value = true
        kotlinx.coroutines.delay(1200)
        _isSyncing.value = false
        val now = System.currentTimeMillis()
        _youtubeConfig.value = _youtubeConfig.value.copy(
            lastSyncTimestampMillis = now,
            lastErrorMessage = "Verifica completata: endpoint YouTube API in attesa di credenziali OAuth nel backend."
        )
        return Result.success("Verifica completata con successo alle ore ${formatTime(now)}")
    }

    private fun formatTime(millis: Long): String {
        val date = java.util.Date(millis)
        val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.ITALY)
        return sdf.format(date)
    }
}
