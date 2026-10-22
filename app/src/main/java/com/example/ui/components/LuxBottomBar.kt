package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxSurfaceBorder
import com.example.ui.theme.LuxSurfaceCard
import com.example.ui.theme.LuxTextMuted
import com.example.ui.theme.LuxTextSecondary

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

val MainNavItems = listOf(
    NavItem(Screen.Dashboard.route, "Dashboard", Icons.Filled.Dashboard, "nav_dashboard"),
    NavItem(Screen.Analytics.route, "Analytics", Icons.Filled.ShowChart, "nav_analytics"),
    NavItem(Screen.Strategy.route, "Strategia", Icons.Filled.AutoAwesome, "nav_strategy"),
    NavItem(Screen.VideoLab.route, "Video Lab", Icons.Filled.VideoLibrary, "nav_videolab"),
    NavItem("hub", "Studio Hub", Icons.Filled.GridView, "nav_hub")
)

@Composable
fun LuxBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(LuxDarkBackground)
            .border(
                width = 1.dp,
                color = LuxSurfaceBorder,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        NavigationBar(
            containerColor = LuxSurfaceCard,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            MainNavItems.forEach { item ->
                val isSelected = currentRoute == item.route || (item.route == "hub" && (
                    currentRoute == Screen.Calendar.route ||
                    currentRoute == Screen.Archive.route ||
                    currentRoute == Screen.Social.route ||
                    currentRoute == Screen.Settings.route
                ))

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LuxGoldPrimary,
                        selectedTextColor = LuxGoldPrimary,
                        indicatorColor = LuxGoldContainer,
                        unselectedIconColor = LuxTextMuted,
                        unselectedTextColor = LuxTextMuted
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}
