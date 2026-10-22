package com.example.ui.screens.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.example.ui.components.LuxConfirmDialog
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

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showResetDialog by remember { mutableStateOf(false) }

    var channelIdInput by remember(state.channelId) { mutableStateOf(state.channelId) }
    var subsThresholdInput by remember(state.requiredSubscribers) { mutableStateOf(state.requiredSubscribers.toString()) }
    var hoursThresholdInput by remember(state.requiredWatchHours) { mutableStateOf(state.requiredWatchHours.toInt().toString()) }

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

            // Settings Header
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Settings, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Impostazioni & Future Integrazioni", color = LuxTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Configurazione architetturale, API e parametri studio", color = LuxTextMuted, fontSize = 11.sp)
                    }
                }
            }

            // Architecture and Security Rules Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxGoldBorder.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Security, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "SICUREZZA E PRIVACY DEL CLIENT ANDROID", color = LuxGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Come richiesto dalle specifiche, questo client nativo non include token privati o chiavi segrete hardcoded nel codice sorgente.\nTutte le chiamate a YouTube Data API v3 e Google Analytics avvengono tramite token OAuth temporanei delegati o backend sicuro.",
                            color = LuxTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // YouTube Data & Analytics API Module
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxSurfaceBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.LinkOff, contentDescription = null, tint = LuxYoutubeRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "YouTube Data API & OAuth 2.0", color = LuxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LuxYoutubeRed.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "Non collegato", color = LuxYoutubeRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedTextField(
                            value = channelIdInput,
                            onValueChange = { channelIdInput = it },
                            label = { Text("ID Canale YouTube", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Scope richiesti in Google Cloud Console:\n• https://www.googleapis.com/auth/youtube.readonly\n• https://www.googleapis.com/auth/yt-analytics.readonly",
                            color = LuxTextMuted,
                            fontSize = 10.sp
                        )

                        Button(
                            onClick = {
                                viewModel.updateChannelId(channelIdInput)
                                viewModel.testApiConnection()
                            },
                            enabled = !state.isSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = LuxSurfaceElevated, contentColor = LuxGoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("test_api_connection_button")
                        ) {
                            if (state.isSyncing) {
                                CircularProgressIndicator(color = LuxGoldPrimary, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(text = "Verifica Connessione Endpoint", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Updatable Monetization Thresholds
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxGoldBorder.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.MonetizationOn, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Soglie Requisiti Monetizzazione (Aggiornabili)", color = LuxGoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Le soglie YouTube Partner Program non sono fisse nel codice. Puoi aggiornarle se YouTube modifica i requisiti (es: 1000 iscritti e 4000 ore oppure livello intermedio 500 iscritti e 3000 ore).",
                            color = LuxTextSecondary,
                            fontSize = 11.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = subsThresholdInput,
                                onValueChange = { subsThresholdInput = it },
                                label = { Text("Soglia Iscritti", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = hoursThresholdInput,
                                onValueChange = { hoursThresholdInput = it },
                                label = { Text("Soglia Ore (h)", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Button(
                            onClick = {
                                val s = subsThresholdInput.toIntOrNull() ?: 1000
                                val h = hoursThresholdInput.toDoubleOrNull() ?: 4000.0
                                viewModel.updateMonetizationThresholds(s, h, 10_000_000L)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary, contentColor = Color(0xFF121006)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Aggiorna Soglie YPP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Sync Schedule & Remote Backend
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxSurfaceBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.CloudSync, contentDescription = null, tint = LuxInfoBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Sincronizzazione Programmata", color = LuxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(text = "Intervallo di controllo metriche in background: ogni ${state.syncIntervalHours} ore.", color = LuxTextMuted, fontSize = 11.sp)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(6, 12, 24).forEach { hours ->
                                val isSelected = state.syncIntervalHours == hours
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) LuxGoldPrimary else LuxSurfaceElevated)
                                        .clickable { viewModel.setSyncIntervalHours(hours) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$hours Ore",
                                        color = if (isSelected) Color(0xFF121006) else LuxTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reset demo data action
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxSurfaceBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Dati Dimostrativi & Ripristino", color = LuxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Ripristina i video di prova e le metriche di esempio per ricalibrare i test dello studio.", color = LuxTextMuted, fontSize = 11.sp)
                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Filled.RestartAlt, contentDescription = null, tint = LuxGoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Ripristina Dati Dimostrativi di Default", color = LuxGoldAccent, fontSize = 12.sp)
                        }
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

    LuxConfirmDialog(
        show = showResetDialog,
        title = "Ripristinare i dati dimostrativi?",
        message = "Questa operazione ricreerà i progetti di esempio Ferrari, Bugatti e Porsche nel database Room locale.",
        confirmButtonText = "Ripristina",
        isDestructive = false,
        onConfirm = {
            viewModel.resetDemoData()
            showResetDialog = false
        },
        onDismiss = { showResetDialog = false }
    )
}
