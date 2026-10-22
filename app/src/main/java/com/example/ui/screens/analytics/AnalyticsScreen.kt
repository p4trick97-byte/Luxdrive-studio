package com.example.ui.screens.analytics

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectFormat
import com.example.data.model.VideoProject
import com.example.ui.components.DataBadgeType
import com.example.ui.components.LuxuryLineChart
import com.example.ui.components.LuxuryMetricCard
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxGoldAccent
import com.example.ui.theme.LuxGoldBorder
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
fun AnalyticsScreen(
    state: AnalyticsUiState,
    viewModel: AnalyticsViewModel,
    onNavigateToSettings: () -> Unit,
    onVideoClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
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

            // Official YouTube API Status Card (Non collegato)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxYoutubeRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LinkOff,
                                contentDescription = null,
                                tint = LuxYoutubeRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "YouTube Data & Analytics API",
                                    color = LuxTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Stato: Non collegato alle API ufficiali",
                                    color = LuxYoutubeRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Button(
                                onClick = onNavigateToSettings,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LuxSurfaceElevated,
                                    contentColor = LuxGoldPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("configure_api_button")
                            ) {
                                Text(text = "Configura", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "L'app non simula finte risposte live di YouTube. Fino alla configurazione del Client OAuth Google in Impostazioni, puoi registrare manualmente le metriche o consultare lo storico locale.",
                            color = LuxTextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Period Filters (7gg, 28gg, 90gg, Tutto)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PERIODO DI ANALISI",
                            color = LuxTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.setShowAddMetricModal(true) }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = LuxGoldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Nuova Rilevazione",
                                color = LuxGoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PeriodFilter.entries.forEach { period ->
                            val isSelected = state.selectedPeriod == period
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) LuxGoldPrimary else LuxSurfaceCard)
                                    .border(
                                        1.dp,
                                        if (isSelected) LuxGoldPrimary else LuxSurfaceBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectPeriod(period) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = period.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF121006) else LuxTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Format tabs (TUTTI, VIDEO LUNGHI, SHORTS)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("TUTTI" to "Tutti i Formati", "LONG" to "Video Lunghi (16:9)", "SHORTS" to "Shorts (9:16)").forEach { (key, label) ->
                        val isSelected = state.selectedFormatTab == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) LuxGoldPrimary.copy(alpha = 0.2f) else LuxSurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) LuxGoldPrimary else LuxSurfaceBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.selectFormatTab(key) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) LuxGoldPrimary else LuxTextMuted
                            )
                        }
                    }
                }
            }

            // Visualizzazioni nel tempo chart
            item {
                val viewsPoints = state.filteredMetrics.map { it.totalViews.toFloat() }
                val labels = state.filteredMetrics.map {
                    SimpleDateFormat("dd/MM", Locale.ITALY).format(Date(it.recordDateMillis))
                }
                LuxuryLineChart(
                    title = "Andamento Visualizzazioni Canale",
                    subtitle = "Storico rilevazioni nel periodo (${state.selectedPeriod.label})",
                    dataPoints = viewsPoints,
                    labels = labels
                )
            }

            // Ore di visualizzazione nel tempo chart
            item {
                val hoursPoints = state.filteredMetrics.map { it.totalWatchHours.toFloat() }
                val labels = state.filteredMetrics.map {
                    SimpleDateFormat("dd/MM", Locale.ITALY).format(Date(it.recordDateMillis))
                }
                LuxuryLineChart(
                    title = "Ore di Visualizzazione (Watch Time)",
                    subtitle = "Progressione verso i requisiti YPP (4.000 ore)",
                    dataPoints = hoursPoints,
                    labels = labels,
                    lineColor = LuxInfoBlue
                )
            }

            // Advanced KPI Grid
            item {
                Text(
                    text = "METRICHE CHIAVE E RETENTION",
                    color = LuxTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LuxuryMetricCard(
                        title = "CTR Impressioni",
                        value = "${state.latestMetric?.ctrPercent ?: 8.4}%",
                        subtitle = "Target benchmark: > 7.0%",
                        badgeType = DataBadgeType.DEMO,
                        modifier = Modifier.weight(1f)
                    )
                    LuxuryMetricCard(
                        title = "Fidelizzazione Media",
                        value = "${state.latestMetric?.retentionRatePercent ?: 51.2}%",
                        subtitle = "Retention media al 50%",
                        badgeType = DataBadgeType.DEMO,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val durationMin = (state.latestMetric?.avgViewDurationSecs ?: 295) / 60
                    val durationSec = (state.latestMetric?.avgViewDurationSecs ?: 295) % 60
                    LuxuryMetricCard(
                        title = "Durata Media",
                        value = "%02d:%02d min".format(durationMin, durationSec),
                        subtitle = "Tempo medio per spettatore",
                        badgeType = DataBadgeType.DEMO,
                        modifier = Modifier.weight(1f)
                    )
                    LuxuryMetricCard(
                        title = "Impressioni Feed",
                        value = state.latestMetric?.impressions?.let { "%,d".format(Locale.ITALY, it) } ?: "320,000",
                        subtitle = "Portata stimata dell'algoritmo",
                        badgeType = DataBadgeType.DEMO,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Monetization & RPM notice card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxGoldBorder.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = LuxGoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ricavi e Metriche Pubblicitarie (RPM / CPM)",
                                color = LuxGoldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ricavi stimati correnti: 0,00 € (Canale non ancora idoneo al Programma Partner YouTube).\nRPM medio stimato di settore Automotive in Italia: 3,50 € - 6,80 € ogni 1.000 visualizzazioni.",
                            color = LuxTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Confronto tra singoli contenuti (Video comparison)
            item {
                Text(
                    text = "CONFRONTO TRA SINGOLI CONTENUTI",
                    color = LuxTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            item {
                if (state.publishedVideos.size >= 2) {
                    val videoA = state.comparisonVideoA ?: state.publishedVideos[0]
                    val videoB = state.comparisonVideoB ?: state.publishedVideos[1]

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(LuxSurfaceCard)
                            .border(1.dp, LuxSurfaceBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CompareArrows,
                                        contentDescription = null,
                                        tint = LuxGoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Test A/B Prestazioni",
                                        color = LuxTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Cambia Video",
                                    color = LuxGoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        // Cycle through videos
                                        val idxA = state.publishedVideos.indexOf(videoA)
                                        val nextA = state.publishedVideos[(idxA + 1) % state.publishedVideos.size]
                                        viewModel.setComparisonVideos(nextA.id, videoB.id)
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Video A column
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "VIDEO A",
                                        color = LuxGoldPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = videoA.title,
                                        color = LuxTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "Views: ${videoA.viewsCount}", color = LuxTextSecondary, fontSize = 11.sp)
                                    Text(text = "Watch time: ${videoA.watchHours} h", color = LuxTextSecondary, fontSize = 11.sp)
                                    Text(text = "CTR: ${videoA.ctrPercent}%", color = LuxTextSecondary, fontSize = 11.sp)
                                    Text(text = "Like: ${videoA.likesCount}", color = LuxTextSecondary, fontSize = 11.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(100.dp)
                                        .background(LuxSurfaceBorder)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // Video B column
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "VIDEO B",
                                        color = LuxInfoBlue,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = videoB.title,
                                        color = LuxTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "Views: ${videoB.viewsCount}", color = LuxTextSecondary, fontSize = 11.sp)
                                    Text(text = "Watch time: ${videoB.watchHours} h", color = LuxTextSecondary, fontSize = 11.sp)
                                    Text(text = "CTR: ${videoB.ctrPercent}%", color = LuxTextSecondary, fontSize = 11.sp)
                                    Text(text = "Like: ${videoB.likesCount}", color = LuxTextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LuxSurfaceCard)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Servono almeno 2 video con metriche registrate per effettuare il confronto.",
                            color = LuxTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Modal to add manual metric snapshot
    if (state.showAddMetricModal) {
        ManualMetricDialog(
            onDismiss = { viewModel.setShowAddMetricModal(false) },
            onSave = { v, s, h, i, c, notes ->
                viewModel.addManualMetricRecord(v, s, h, i, c, notes)
            }
        )
    }
}

@Composable
fun ManualMetricDialog(
    onDismiss: () -> Unit,
    onSave: (Long, Int, Double, Long, Double, String) -> Unit
) {
    var viewsText by remember { mutableStateOf("") }
    var subsText by remember { mutableStateOf("") }
    var hoursText by remember { mutableStateOf("") }
    var impressionsText by remember { mutableStateOf("") }
    var ctrText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LuxSurfaceElevated,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Registra Dati YouTube Studio",
                color = LuxTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Inserisci le metriche reali copiate dal tuo pannello YouTube Studio:",
                    color = LuxTextMuted,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = viewsText,
                    onValueChange = { viewsText = it },
                    label = { Text("Visualizzazioni totali", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subsText,
                    onValueChange = { subsText = it },
                    label = { Text("Numero iscritti", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hoursText,
                    onValueChange = { hoursText = it },
                    label = { Text("Ore visualizzazione (es. 1320.5)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ctrText,
                    onValueChange = { ctrText = it },
                    label = { Text("CTR impressioni % (es. 8.2)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Note rilevazione", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val views = viewsText.toLongOrNull() ?: 0L
                    val subs = subsText.toIntOrNull() ?: 0
                    val hours = hoursText.toDoubleOrNull() ?: 0.0
                    val imp = impressionsText.toLongOrNull() ?: (views * 10)
                    val ctr = ctrText.toDoubleOrNull() ?: 5.0
                    onSave(views, subs, hours, imp, ctr, notesText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary, contentColor = Color(0xFF121006)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salva Rilevazione", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Annulla", color = LuxTextMuted)
            }
        }
    )
}
