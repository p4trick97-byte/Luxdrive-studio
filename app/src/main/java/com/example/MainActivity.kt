package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.LuxDriveDatabase
import com.example.data.repository.AiStrategyRepository
import com.example.data.repository.AnalyticsRepository
import com.example.data.repository.IntegrationBridgeManager
import com.example.data.repository.VideoProjectRepository
import com.example.ui.components.LuxBottomBar
import com.example.ui.components.LuxTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.analytics.AnalyticsScreen
import com.example.ui.screens.analytics.AnalyticsViewModel
import com.example.ui.screens.archive.ArchiveScreen
import com.example.ui.screens.archive.ArchiveViewModel
import com.example.ui.screens.calendar.CalendarViewModel
import com.example.ui.screens.calendar.EditorialCalendarScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dashboard.DashboardViewModel
import com.example.ui.screens.hub.StudioHubScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.settings.SettingsViewModel
import com.example.ui.screens.social.SocialDistributionScreen
import com.example.ui.screens.social.SocialViewModel
import com.example.ui.screens.strategy.AiStrategyScreen
import com.example.ui.screens.strategy.AiStrategyViewModel
import com.example.ui.screens.videolab.VideoLabDetailScreen
import com.example.ui.screens.videolab.VideoLabScreen
import com.example.ui.screens.videolab.VideoLabViewModel
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxDriveTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = LuxDriveDatabase.getDatabase(applicationContext)
        val projectRepository = VideoProjectRepository(database.videoProjectDao())
        val analyticsRepository = AnalyticsRepository(database.channelMetricDao())
        val strategyRepository = AiStrategyRepository()
        val integrationBridge = IntegrationBridgeManager()

        setContent {
            LuxDriveTheme {
                LuxDriveApp(
                    projectRepository = projectRepository,
                    analyticsRepository = analyticsRepository,
                    strategyRepository = strategyRepository,
                    integrationBridge = integrationBridge
                )
            }
        }
    }
}

@Composable
fun LuxDriveApp(
    projectRepository: VideoProjectRepository,
    analyticsRepository: AnalyticsRepository,
    strategyRepository: AiStrategyRepository,
    integrationBridge: IntegrationBridgeManager
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ViewModels with factory
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DashboardViewModel(projectRepository, analyticsRepository, integrationBridge) as T
            }
        }
    )

    val analyticsViewModel: AnalyticsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AnalyticsViewModel(analyticsRepository, projectRepository, integrationBridge) as T
            }
        }
    )

    val strategyViewModel: AiStrategyViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AiStrategyViewModel(strategyRepository, projectRepository, analyticsRepository) as T
            }
        }
    )

    val videoLabViewModel: VideoLabViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return VideoLabViewModel(projectRepository) as T
            }
        }
    )

    val calendarViewModel: CalendarViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CalendarViewModel(projectRepository) as T
            }
        }
    )

    val archiveViewModel: ArchiveViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ArchiveViewModel(projectRepository) as T
            }
        }
    )

    val socialViewModel: SocialViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SocialViewModel(projectRepository) as T
            }
        }
    )

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(analyticsRepository, projectRepository, integrationBridge) as T
            }
        }
    )

    val dashboardState by dashboardViewModel.uiState.collectAsState()
    val analyticsState by analyticsViewModel.uiState.collectAsState()
    val strategyState by strategyViewModel.uiState.collectAsState()
    val videoLabState by videoLabViewModel.state.collectAsState()
    val calendarState by calendarViewModel.uiState.collectAsState()
    val archiveState by archiveViewModel.uiState.collectAsState()
    val socialState by socialViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()

    val topBarTitle = when {
        currentRoute == Screen.Dashboard.route -> "Dashboard Canale"
        currentRoute == Screen.Analytics.route -> "YouTube Analytics"
        currentRoute == Screen.Strategy.route -> "Strategia Automotive AI"
        currentRoute == Screen.VideoLab.route -> "Video Lab Studio"
        currentRoute == "hub" -> "Studio Hub"
        currentRoute == Screen.Calendar.route -> "Calendario Editoriale"
        currentRoute == Screen.Archive.route -> "Archivio Contenuti"
        currentRoute == Screen.Social.route -> "Distribuzione Social"
        currentRoute == Screen.Settings.route -> "Impostazioni"
        currentRoute?.startsWith("videolab_detail") == true -> "Editor Progetto"
        else -> "LuxDrive AI Studio"
    }

    Scaffold(
        containerColor = LuxDarkBackground,
        topBar = {
            LuxTopBar(
                title = topBarTitle,
                isSyncing = dashboardState.isSyncing,
                isConnected = dashboardState.youtubeConfig.status.isConnected,
                onSyncClick = { dashboardViewModel.triggerSync() },
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        bottomBar = {
            if (currentRoute?.startsWith("videolab_detail") != true) {
                LuxBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    state = dashboardState,
                    onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                    onNavigateToStrategy = { navController.navigate(Screen.Strategy.route) },
                    onNavigateToVideoLab = { navController.navigate(Screen.VideoLab.route) },
                    onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                    onNavigateToArchive = { navController.navigate(Screen.Archive.route) },
                    onNavigateToSocial = { navController.navigate(Screen.Social.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onProjectClick = { projectId ->
                        navController.navigate(Screen.VideoLabDetail.createRoute(projectId))
                    }
                )
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    state = analyticsState,
                    viewModel = analyticsViewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onVideoClick = { videoId ->
                        navController.navigate(Screen.VideoLabDetail.createRoute(videoId))
                    }
                )
            }

            composable(Screen.Strategy.route) {
                AiStrategyScreen(
                    state = strategyState,
                    viewModel = strategyViewModel,
                    onNavigateToVideoLabDetail = { projectId ->
                        navController.navigate(Screen.VideoLabDetail.createRoute(projectId))
                    }
                )
            }

            composable(Screen.VideoLab.route) {
                VideoLabScreen(
                    state = videoLabState,
                    viewModel = videoLabViewModel,
                    onNavigateToDetail = { projectId ->
                        navController.navigate(Screen.VideoLabDetail.createRoute(projectId))
                    }
                )
            }

            composable("hub") {
                StudioHubScreen(
                    onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                    onNavigateToArchive = { navController.navigate(Screen.Archive.route) },
                    onNavigateToSocial = { navController.navigate(Screen.Social.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Calendar.route) {
                EditorialCalendarScreen(
                    state = calendarState,
                    viewModel = calendarViewModel,
                    onNavigateToProject = { projectId ->
                        navController.navigate(Screen.VideoLabDetail.createRoute(projectId))
                    },
                    onNavigateToVideoLab = { navController.navigate(Screen.VideoLab.route) }
                )
            }

            composable(Screen.Archive.route) {
                ArchiveScreen(
                    state = archiveState,
                    viewModel = archiveViewModel,
                    onNavigateToProject = { projectId ->
                        navController.navigate(Screen.VideoLabDetail.createRoute(projectId))
                    }
                )
            }

            composable(Screen.Social.route) {
                SocialDistributionScreen(
                    state = socialState,
                    viewModel = socialViewModel,
                    onNavigateToProject = { projectId ->
                        navController.navigate(Screen.VideoLabDetail.createRoute(projectId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    state = settingsState,
                    viewModel = settingsViewModel
                )
            }

            composable(
                route = Screen.VideoLabDetail.route,
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
                VideoLabDetailScreen(
                    projectId = projectId,
                    viewModel = videoLabViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
