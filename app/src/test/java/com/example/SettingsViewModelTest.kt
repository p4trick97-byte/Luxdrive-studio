package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.LuxDriveDatabase
import com.example.data.repository.AnalyticsRepository
import com.example.data.repository.IntegrationBridgeManager
import com.example.data.repository.VideoProjectRepository
import com.example.data.service.GeminiAutomotiveAiClient
import com.example.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var aiClient: GeminiAutomotiveAiClient

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = LuxDriveDatabase.getDatabase(context)
        val projectRepo = VideoProjectRepository(db.videoProjectDao())
        val analyticsRepo = AnalyticsRepository(db.channelMetricDao())
        val bridge = IntegrationBridgeManager()
        aiClient = GeminiAutomotiveAiClient(context)

        viewModel = SettingsViewModel(
            analyticsRepository = analyticsRepo,
            projectRepository = projectRepo,
            integrationBridge = bridge,
            aiClient = aiClient
        )
    }

    @Test
    fun defaultState_hasGemini38FlashSelected() {
        assertEquals("gemini-3.8-flash", viewModel.uiState.value.selectedGeminiTestModel)
        assertFalse(viewModel.uiState.value.isTestingGemini)
        assertNull(viewModel.uiState.value.geminiTestResult)
    }

    @Test
    fun selectGeminiTestModel_switchesModelAndResetsPreviousResult() {
        viewModel.selectGeminiTestModel("gemini-3.5-flash-lite")
        assertEquals("gemini-3.5-flash-lite", viewModel.uiState.value.selectedGeminiTestModel)
        assertNull(viewModel.uiState.value.geminiTestResult)

        viewModel.selectGeminiTestModel("gemini-3.8-flash")
        assertEquals("gemini-3.8-flash", viewModel.uiState.value.selectedGeminiTestModel)
    }
}
