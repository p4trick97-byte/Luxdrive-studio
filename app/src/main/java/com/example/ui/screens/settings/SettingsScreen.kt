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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LuxConfirmDialog
import com.example.util.AppCheckTokenState
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

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var customTokenInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.initAppCheckStatus(context)
    }

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

            // Firebase App Check & Gemini AI Logic Card
            item {
                val diag = state.appCheckDiagnostics
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxGoldBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.VerifiedUser, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Firebase App Check & AI Logic", color = LuxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            val providerBadge = if (diag?.isDebugMode == true) "Debug Provider" else "Play Integrity"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LuxGoldPrimary.copy(alpha = 0.15f))
                                    .border(0.5.dp, LuxGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = providerBadge, color = LuxGoldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = "Protegge le chiamate a Gemini Developer API contro abusi. In sviluppo da smartphone, il debug secret del Galaxy S23 deve essere aggiunto ai token autorizzati nella Firebase Console.",
                            color = LuxTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        // Debug secret info box (if in debug)
                        if (diag?.isDebugMode == true && !diag.debugSecret.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LuxSurfaceElevated)
                                    .border(0.5.dp, LuxSurfaceBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = "Debug Secret Dispositivo (Galaxy S23):", color = LuxTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(text = diag.debugSecret, color = LuxGoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(diag.debugSecret))
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp).testTag("copy_debug_token_button")
                                        ) {
                                            Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "Copia Secret", color = LuxGoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Token Status banner
                        when (val ts = diag?.tokenState) {
                            is AppCheckTokenState.TokenValid -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LuxGreenGrowth.copy(alpha = 0.12f))
                                        .border(0.5.dp, LuxGreenGrowth.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = LuxGreenGrowth, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "Attestazione Attiva: il server Firebase accetta le chiamate AI da questo dispositivo.", color = LuxGreenGrowth, fontSize = 10.sp, lineHeight = 14.sp)
                                    }
                                }
                            }
                            is AppCheckTokenState.TokenRejectedByServer -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LuxYoutubeRed.copy(alpha = 0.12f))
                                        .border(0.5.dp, LuxYoutubeRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Filled.ErrorOutline, contentDescription = null, tint = LuxYoutubeRed, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "Token rifiutato dal server (Token is invalid)", color = LuxYoutubeRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = "Il secret del tuo telefono non è registrato in Firebase Console:\n" +
                                                "1. Tocca 'Copia Secret' sopra.\n" +
                                                "2. Apri Firebase Console nel browser -> App Check -> Scheda App -> tre puntini su 'com.aistudio.luxdrive.zqkpmx' -> Gestisci token di debug.\n" +
                                                "3. Incolla il token e premi Salva. Poi tocca Verifica qui sotto.",
                                            color = LuxTextSecondary,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                            is AppCheckTokenState.TokenError -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LuxWarningAmber.copy(alpha = 0.12f))
                                        .border(0.5.dp, LuxWarningAmber.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(text = ts.error, color = LuxWarningAmber, fontSize = 10.sp)
                                }
                            }
                            else -> {}
                        }

                        // Button to verify
                        Button(
                            onClick = { viewModel.verifyAppCheck(context, forceRefresh = true) },
                            enabled = !state.isVerifyingAppCheck,
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary, contentColor = Color(0xFF121006)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("verify_app_check_button")
                        ) {
                            if (state.isVerifyingAppCheck) {
                                CircularProgressIndicator(color = Color(0xFF121006), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Verifica attestazione con il server...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Verifica Attestazione App Check", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Optional: input custom debug token from console
                        if (diag?.isDebugMode == true) {
                            var showManualInput by remember { mutableStateOf(false) }
                            if (!showManualInput) {
                                Text(
                                    text = "Hai generato un token direttamente dalla Firebase Console? Tocca qui per inserirlo",
                                    color = LuxGoldAccent,
                                    fontSize = 10.sp,
                                    modifier = Modifier.clickable { showManualInput = true }
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedTextField(
                                        value = customTokenInput,
                                        onValueChange = { customTokenInput = it },
                                        label = { Text("Incolla Debug Token da Firebase Console", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                viewModel.saveCustomDebugSecret(context, customTokenInput)
                                                showManualInput = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary, contentColor = Color(0xFF121006)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Salva Token", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(
                                            onClick = { showManualInput = false },
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Annulla", color = LuxTextMuted, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Gemini AI Developer API Connection Test Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxGoldBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = LuxGoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Test Connessione Gemini",
                                    color = LuxTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LuxGoldPrimary.copy(alpha = 0.15f))
                                    .border(0.5.dp, LuxGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Firebase AI Logic",
                                    color = LuxGoldPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "Invia una richiesta minima ('Rispondi esclusivamente con OK') per diagnosticare lo stato dell'inferenza, la latenza di rete e la risposta del server Firebase prima di lanciare la generazione completa.",
                            color = LuxTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Text(
                            text = "Seleziona il modello da testare:",
                            color = LuxTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val models = listOf(
                                "gemini-3.8-flash" to "gemini-3.8-flash\n(Predefinito)",
                                "gemini-3.5-flash-lite" to "gemini-3.5-flash-lite\n(Alternativo)"
                            )
                            models.forEach { (modelId, label) ->
                                val isSelected = state.selectedGeminiTestModel == modelId
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) LuxGoldPrimary.copy(alpha = 0.18f) else LuxSurfaceElevated)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.5.dp,
                                            color = if (isSelected) LuxGoldPrimary else LuxSurfaceBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable(enabled = !state.isTestingGemini) {
                                            viewModel.selectGeminiTestModel(modelId)
                                        }
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) LuxGoldAccent else LuxTextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.testGeminiConnection() },
                            enabled = !state.isTestingGemini,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuxGoldPrimary,
                                contentColor = Color(0xFF121006)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_gemini_connection_button")
                        ) {
                            if (state.isTestingGemini) {
                                CircularProgressIndicator(
                                    color = Color(0xFF121006),
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Invio richiesta minima in corso...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF121006),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Test connessione Gemini",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Detailed result box
                        state.geminiTestResult?.let { res ->
                            if (res.isSuccess) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LuxGreenGrowth.copy(alpha = 0.12f))
                                        .border(0.5.dp, LuxGreenGrowth.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                tint = LuxGreenGrowth,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Inferenza completata con successo!",
                                                color = LuxGreenGrowth,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "• Modello: ${res.modelName}\n• Durata: ${res.durationMs} ms\n• Risposta server: \"${res.responseText}\"",
                                            color = LuxTextSecondary,
                                            fontSize = 10.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LuxYoutubeRed.copy(alpha = 0.12f))
                                        .border(0.5.dp, LuxYoutubeRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.ErrorOutline,
                                                contentDescription = null,
                                                tint = LuxYoutubeRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Richiesta non riuscita (durata: ${res.durationMs} ms)",
                                                color = LuxYoutubeRed,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "Modello testato: ${res.modelName}\nErrore SDK originale:\n${res.errorMessage}",
                                            color = LuxTextSecondary,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
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
