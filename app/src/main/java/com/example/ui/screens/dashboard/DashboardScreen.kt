package com.example.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ProjectFormat
import com.example.data.model.VideoProject
import com.example.ui.components.DataBadgeType
import com.example.ui.components.LuxuryMetricCard
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxGoldAccent
import com.example.ui.theme.LuxGoldBorder
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGreenGrowth
import com.example.ui.theme.LuxInfoBlue
import com.example.ui.theme.LuxSurfaceBorder
import com.example.ui.theme.LuxSurfaceCard
import com.example.ui.theme.LuxSurfaceElevated
import com.example.ui.theme.LuxTextMuted
import com.example.ui.theme.LuxTextPrimary
import com.example.ui.theme.LuxTextSecondary
import com.example.ui.theme.LuxWarningAmber
import com.example.ui.theme.LuxYoutubeRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToVideoLab: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToSocial: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onProjectClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(LuxDarkBackground)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Hero studio banner with generated luxury hypercar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, LuxGoldBorder.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_automotive_studio),
                        contentDescription = "LuxDrive Studio Hypercar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay for contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        LuxDarkBackground.copy(alpha = 0.85f),
                                        LuxDarkBackground
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(LuxYoutubeRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CANALE YOUTUBE: LUXDRIVE ITALIA",
                                color = LuxGoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Automotive Strategy & Production",
                            color = LuxTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // Sync Status & Connection Notice
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxSurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = LuxGoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Stato Sincronizzazione YouTube API",
                                color = LuxTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = state.youtubeConfig.lastErrorMessage
                                    ?: "Integrazione API pronta per credenziali OAuth.",
                                color = LuxTextMuted,
                                fontSize = 11.sp,
                                maxLines = 2
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LuxWarningAmber.copy(alpha = 0.15f))
                                .border(0.5.dp, LuxWarningAmber.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Dati Demo",
                                color = LuxWarningAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // High-impact Metric Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val viewsVal = state.latestMetric?.totalViews?.let { "%,d".format(Locale.ITALY, it) } ?: "0"
                    val subsVal = state.latestMetric?.totalSubscribers?.let { "%,d".format(Locale.ITALY, it) } ?: "0"

                    LuxuryMetricCard(
                        title = "Visualizzazioni",
                        value = viewsVal,
                        deltaText = "+38.4%",
                        isPositiveDelta = true,
                        badgeType = DataBadgeType.DEMO,
                        icon = Icons.Filled.Visibility,
                        modifier = Modifier.weight(1f)
                    )

                    LuxuryMetricCard(
                        title = "Iscritti Canale",
                        value = subsVal,
                        deltaText = "+42 nell'ultimo mese",
                        isPositiveDelta = true,
                        badgeType = DataBadgeType.DEMO,
                        icon = Icons.Filled.ShowChart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val hoursVal = state.latestMetric?.totalWatchHours?.let { "%.1f h".format(Locale.ITALY, it) } ?: "0.0 h"

                    LuxuryMetricCard(
                        title = "Ore Visualizzazione",
                        value = hoursVal,
                        subtitle = "Obiettivo YPP: 4.000 h",
                        badgeType = DataBadgeType.DEMO,
                        icon = Icons.Filled.Schedule,
                        modifier = Modifier.weight(1f)
                    )

                    LuxuryMetricCard(
                        title = "Video Pubblicati",
                        value = "${state.publishedCount}",
                        subtitle = "${state.inProgressCount} progetti attivi",
                        badgeType = DataBadgeType.REAL,
                        icon = Icons.Filled.PlayCircleOutline,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Monetization Progress Box (YPP Partner Program)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(LuxGoldContainer, LuxSurfaceCard)
                            )
                        )
                        .border(1.dp, LuxGoldBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = LuxGoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "MONETIZZAZIONE YOUTUBE",
                                    color = LuxGoldPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Non Monetizzato (In Crescita)",
                                color = LuxWarningAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Watch hours progress bar
                        Text(
                            text = "Ore di visualizzazione pubbliche: ${state.monetizationStatus.currentWatchHours.toInt()} / ${state.monetizationStatus.requiredWatchHours.toInt()} h",
                            color = LuxTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { state.monetizationStatus.watchHoursProgressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = LuxGoldPrimary,
                            trackColor = LuxSurfaceBorder
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Subscribers progress bar
                        Text(
                            text = "Iscritti: ${state.monetizationStatus.currentSubscribers} / ${state.monetizationStatus.requiredSubscribers}",
                            color = LuxTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { state.monetizationStatus.subscribersProgressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = LuxGreenGrowth,
                            trackColor = LuxSurfaceBorder
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Stima al traguardo basata sull'andamento attuale: circa ${state.monetizationStatus.estimatedDaysToThreshold} giorni.",
                            color = LuxTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Quick studio actions shortcuts
            item {
                Text(
                    text = "STUDIO QUICK ACTIONS",
                    color = LuxTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Strategia AI",
                        desc = "Trova nuove idee",
                        icon = Icons.Filled.AutoAwesome,
                        accentColor = LuxGoldPrimary,
                        onClick = onNavigateToStrategy,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Nuovo Video",
                        desc = "Apri Video Lab",
                        icon = Icons.Filled.VideoLibrary,
                        accentColor = LuxInfoBlue,
                        onClick = onNavigateToVideoLab,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Calendario",
                        desc = "Timeline uscite",
                        icon = Icons.Filled.CalendarMonth,
                        accentColor = LuxGreenGrowth,
                        onClick = onNavigateToCalendar,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "YouTube Stats",
                        desc = "Retention & CTR",
                        icon = Icons.Filled.ShowChart,
                        accentColor = LuxYoutubeRed,
                        onClick = onNavigateToAnalytics,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Prossime Pubblicazioni
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROSSIME PUBBLICAZIONI",
                        color = LuxTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Vedi Calendario",
                        color = LuxGoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToCalendar() }
                    )
                }
            }

            if (state.nextScheduledProjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LuxSurfaceCard)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nessun video programmato nelle prossime date. Aggiungine uno dal Video Lab!",
                            color = LuxTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(state.nextScheduledProjects) { project ->
                    ScheduledVideoItem(
                        project = project,
                        onClick = { onProjectClick(project.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    desc: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LuxSurfaceCard)
            .border(1.dp, LuxSurfaceBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = LuxTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = LuxTextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ScheduledVideoItem(
    project: VideoProject,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = project.scheduledDateMillis?.let {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ITALY).format(Date(it))
    } ?: "Data non definita"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LuxSurfaceCard)
            .border(1.dp, LuxGoldBorder.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (project.format == ProjectFormat.SHORTS.name) LuxYoutubeRed.copy(alpha = 0.15f) else LuxGoldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (project.format == ProjectFormat.SHORTS.name) Icons.Filled.PlayCircleOutline else Icons.Filled.VideoLibrary,
                    contentDescription = null,
                    tint = if (project.format == ProjectFormat.SHORTS.name) LuxYoutubeRed else LuxGoldPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    color = LuxTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = project.category,
                        color = LuxGoldAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = " • $dateStr",
                        color = LuxTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Dettaglio",
                tint = LuxTextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
