package com.example.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AnalyticsRepository
import com.example.data.repository.IntegrationBridgeManager
import com.example.data.repository.VideoProjectRepository
import com.example.data.repository.YouTubeApiConfig
import com.example.data.service.GeminiAutomotiveAiClient
import com.example.data.service.GeminiTestResult
import com.example.util.AppCheckDiagnostics
import com.example.util.AppCheckHelper
import com.example.util.AppCheckTokenState
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
    val feedbackMessage: String? = null,
    // App Check diagnostics
    val appCheckDiagnostics: AppCheckDiagnostics? = null,
    val isVerifyingAppCheck: Boolean = false,
    // Gemini AI connection test
    val selectedGeminiTestModel: String = "gemini-3.8-flash",
    val isTestingGemini: Boolean = false,
    val geminiTestResult: GeminiTestResult? = null
)

class SettingsViewModel(
    private val analyticsRepository: AnalyticsRepository,
    private val projectRepository: VideoProjectRepository,
    private val integrationBridge: IntegrationBridgeManager,
    private val aiClient: GeminiAutomotiveAiClient? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun selectGeminiTestModel(modelName: String) {
        _uiState.value = _uiState.value.copy(
            selectedGeminiTestModel = modelName,
            geminiTestResult = null
        )
    }

    fun testGeminiConnection() {
        if (_uiState.value.isTestingGemini) return
        val client = aiClient
        if (client == null) {
            _uiState.value = _uiState.value.copy(
                feedbackMessage = "Client AI non disponibile per il test."
            )
            return
        }
        val model = _uiState.value.selectedGeminiTestModel
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isTestingGemini = true,
                geminiTestResult = null
            )
            val result = client.testConnection(model)
            _uiState.value = _uiState.value.copy(
                isTestingGemini = false,
                geminiTestResult = result,
                feedbackMessage = if (result.isSuccess) {
                    "Test Gemini completato con successo in ${result.durationMs}ms!"
                } else {
                    "Test Gemini fallito: ${result.errorMessage ?: "Errore"}"
                }
            )
        }
    }

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

    fun initAppCheckStatus(context: Context) {
        if (_uiState.value.appCheckDiagnostics != null) return
        val configured = AppCheckHelper.isFirebaseConfigured(context)
        val debugSecret = AppCheckHelper.getOrCreateDebugSecret(context)
        _uiState.value = _uiState.value.copy(
            appCheckDiagnostics = AppCheckDiagnostics(
                isFirebaseConfigured = configured,
                firebaseProjectId = "luxdrive-ai-studio",
                isDebugMode = com.example.BuildConfig.DEBUG,
                activeProviderName = if (com.example.BuildConfig.DEBUG) "DebugAppCheckProviderFactory" else "PlayIntegrityAppCheckProviderFactory",
                debugSecret = debugSecret,
                tokenState = AppCheckTokenState.NotChecked
            )
        )
    }

    fun verifyAppCheck(context: Context, forceRefresh: Boolean = true) {
        if (_uiState.value.isVerifyingAppCheck) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isVerifyingAppCheck = true)
            val diag = AppCheckHelper.verifyAppCheckToken(context, forceRefresh)
            val msg = when (diag.tokenState) {
                is AppCheckTokenState.TokenValid -> "Attestazione App Check verificata con successo dal server Firebase!"
                is AppCheckTokenState.TokenRejectedByServer -> "Attestazione rifiutata: registra il Debug Secret nella console Firebase."
                is AppCheckTokenState.TokenError -> "Errore App Check: ${(diag.tokenState as AppCheckTokenState.TokenError).error}"
                else -> null
            }
            _uiState.value = _uiState.value.copy(
                isVerifyingAppCheck = false,
                appCheckDiagnostics = diag,
                feedbackMessage = msg
            )
        }
    }

    fun saveCustomDebugSecret(context: Context, secret: String) {
        val trimmed = secret.trim()
        if (trimmed.isBlank()) {
            _uiState.value = _uiState.value.copy(feedbackMessage = "Il token non può essere vuoto.")
            return
        }
        val ok = AppCheckHelper.setCustomDebugSecret(context, trimmed)
        if (ok) {
            _uiState.value = _uiState.value.copy(
                appCheckDiagnostics = _uiState.value.appCheckDiagnostics?.copy(
                    debugSecret = trimmed,
                    tokenState = AppCheckTokenState.NotChecked
                ),
                feedbackMessage = "Nuovo Debug Secret salvato localmente! Ora verifica il token."
            )
        } else {
            _uiState.value = _uiState.value.copy(feedbackMessage = "Errore durante il salvataggio del token.")
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
