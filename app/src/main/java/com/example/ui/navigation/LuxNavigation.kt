package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object Analytics : Screen("analytics", "Analytics")
    data object Strategy : Screen("strategy", "Strategia AI")
    data object VideoLab : Screen("videolab", "Video Lab")
    data object VideoLabDetail : Screen("videolab_detail/{projectId}", "Editor Video") {
        fun createRoute(projectId: Long): String = "videolab_detail/$projectId"
    }
    data object Calendar : Screen("calendar", "Calendario")
    data object Archive : Screen("archive", "Archivio")
    data object Social : Screen("social", "Altri Social")
    data object Settings : Screen("settings", "Impostazioni")
}
