package com.example.ui.screens.videolab

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat
import com.example.data.model.ProjectOrigin
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import com.example.data.service.AiBackendStatus
import com.example.data.service.AiScriptRequest
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
fun VideoLabScreen(
    state: VideoLabUiState,
    viewModel: VideoLabViewModel,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var projectToDeleteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    LaunchedEffect(state.newlyCreatedProjectId) {
        state.newlyCreatedProjectId?.let { newId ->
            onNavigateToDetail(newId)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(LuxDarkBackground)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Hero AI Generation Call-To-Action Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LuxGoldContainer)
                        .border(1.5.dp, LuxGoldBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
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
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GENERATORE AI SCRIPT & MONTAGGIO",
                                    color = LuxGoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Integration indicator badge
                            val (badgeText, badgeColor) = when (state.aiBackendStatus) {
                                is AiBackendStatus.FirebaseAiLogicActive -> "Firebase AI Logic Attivo" to LuxGreenGrowth
                                is AiBackendStatus.NotConfigured -> "Integrazione da Configurare" to LuxWarningAmber
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(badgeColor.copy(alpha = 0.15f))
                                    .border(0.5.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = badgeText, color = badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = "Crea un nuovo video completo con Gemini",
                            color = LuxTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Genera script narrativo proporzionato ai minuti scelti, hook, storyboard b-roll, istruzioni CapCut e adattamenti Shorts/Reels senza testi finti.",
                            color = LuxTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setShowAiGeneratorDialog(true) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LuxGoldPrimary,
                                    contentColor = Color(0xFF121006)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("open_ai_generator_button")
                            ) {
                                Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Genera con AI", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.createNewManualProject(ProjectFormat.LONG_VIDEO) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("create_manual_long_button")
                            ) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = LuxGoldAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Bozza Manuale", color = LuxGoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Search box
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Cerca per titolo, modello, categoria o tag...", color = LuxTextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = LuxGoldPrimary) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("video_lab_search_input")
                )
            }

            // Status filter chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        val isAll = state.selectedStatusFilter == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isAll) LuxGoldPrimary else LuxSurfaceCard)
                                .border(1.dp, if (isAll) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(16.dp))
                                .clickable { viewModel.setStatusFilter(null) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Tutti gli Stati",
                                color = if (isAll) Color(0xFF121006) else LuxTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    items(ProjectStatus.entries) { st ->
                        val isSelected = state.selectedStatusFilter == st.name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) LuxGoldPrimary else LuxSurfaceCard)
                                .border(1.dp, if (isSelected) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(16.dp))
                                .clickable { viewModel.setStatusFilter(st.name) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = st.displayName,
                                color = if (isSelected) Color(0xFF121006) else LuxTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Format filter chips (Tutti, 16:9, 9:16)
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(null to "Tutti i Formati", ProjectFormat.LONG_VIDEO.name to "16:9 Long Video", ProjectFormat.SHORTS.name to "9:16 Shorts").forEach { (fmt, label) ->
                        val isSel = state.selectedFormatFilter == fmt
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) LuxGoldPrimary.copy(alpha = 0.2f) else LuxSurfaceCard)
                                .border(1.dp, if (isSel) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setFormatFilter(fmt) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) LuxGoldPrimary else LuxTextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Projects List
            if (state.projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(LuxSurfaceCard)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Filled.VideoLibrary, contentDescription = null, tint = LuxTextMuted, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Nessun progetto trovato per i filtri selezionati.", color = LuxTextSecondary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.setShowAiGeneratorDialog(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary, contentColor = Color(0xFF121006)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Genera Primo Script con AI", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else {
                items(state.projects) { project ->
                    VideoProjectCard(
                        project = project,
                        onClick = { onNavigateToDetail(project.id) },
                        onDuplicate = { viewModel.duplicateProject(project.id) },
                        onDelete = { projectToDeleteId = project.id }
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

    // AI Creation Modal Dialog
    if (state.showAiGeneratorDialog) {
        AiScriptGeneratorModal(
            backendStatus = state.aiBackendStatus,
            isGenerating = state.isGeneratingAi,
            generationProgress = state.aiGenerationProgress,
            errorMessage = state.aiGenerationError,
            initialRequest = state.lastAiRequest,
            onDismiss = {
                viewModel.setShowAiGeneratorDialog(false)
                viewModel.clearAiError()
            },
            onGenerate = { request ->
                viewModel.generateScriptWithAi(request)
            }
        )
    }

    // Confirmation dialog before deleting
    LuxConfirmDialog(
        show = projectToDeleteId != null,
        title = "Eliminare questo progetto?",
        message = "Questa azione rimuoverà definitivamente lo script, lo storyboard e tutte le note di montaggio dal database locale.",
        confirmButtonText = "Elimina Definitivamente",
        isDestructive = true,
        onConfirm = {
            projectToDeleteId?.let { viewModel.deleteProject(it) }
            projectToDeleteId = null
        },
        onDismiss = { projectToDeleteId = null }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiScriptGeneratorModal(
    backendStatus: AiBackendStatus,
    isGenerating: Boolean,
    generationProgress: String? = null,
    errorMessage: String?,
    initialRequest: AiScriptRequest?,
    onDismiss: () -> Unit,
    onGenerate: (AiScriptRequest) -> Unit
) {
    var carTopic by remember { mutableStateOf(initialRequest?.carTopic ?: "") }
    var selectedFormat by remember { mutableStateOf(initialRequest?.format ?: ProjectFormat.LONG_VIDEO) }
    var durationMinutes by remember { mutableIntStateOf(initialRequest?.durationMinutes ?: if (selectedFormat == ProjectFormat.LONG_VIDEO) 8 else 1) }
    var contentGoal by remember { mutableStateOf(initialRequest?.contentGoal ?: "Analisi costi reali, manutenzione e tenuta del valore") }
    var selectedCategory by remember { mutableStateOf(initialRequest?.category ?: AutomotiveCategory.SUPERCAR_MAINTENANCE_REAL_COST) }
    var narrativeTone by remember { mutableStateOf(initialRequest?.narrativeTone ?: "Autorevole & Analitico") }
    var userSources by remember { mutableStateOf(initialRequest?.userSourcesOrNotes ?: "") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isGenerating) onDismiss() },
        containerColor = LuxSurfaceElevated,
        shape = RoundedCornerShape(18.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Genera Script con AI",
                        color = LuxTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (!isGenerating) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Chiudi", tint = LuxTextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Backend integration status indicator
                item {
                    val (statusText, statusColor) = when (backendStatus) {
                        is AiBackendStatus.FirebaseAiLogicActive -> "Connessione: Firebase AI Logic attivo (Gemini 3.5 Flash Lite)" to LuxGreenGrowth
                        is AiBackendStatus.NotConfigured -> "Integrazione non configurata (Richiede google-services.json)" to LuxWarningAmber
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .border(0.5.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = statusText, color = statusColor, fontSize = 10.sp, lineHeight = 14.sp)
                        }
                    }
                }

                // Streaming / Generation Progress indicator banner
                if (isGenerating) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(LuxGoldPrimary.copy(alpha = 0.12f))
                                .border(1.dp, LuxGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(color = LuxGoldPrimary, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "Elaborazione Gemini 3.5 Flash Lite attiva", color = LuxGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = generationProgress ?: "Connessione ed elaborazione...", color = LuxTextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // Real Error message box if failed
                if (!errorMessage.isNullOrBlank()) {
                    item {
                        val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(LuxYoutubeRed.copy(alpha = 0.15f))
                                .border(1.dp, LuxYoutubeRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Filled.ErrorOutline, contentDescription = null, tint = LuxYoutubeRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Dettaglio Errore Generazione:", color = LuxYoutubeRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(text = errorMessage, color = LuxTextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                                if (errorMessage.contains("App Check", ignoreCase = true) || errorMessage.contains("debug", ignoreCase = true)) {
                                    val secret = com.example.util.AppCheckHelper.getOrCreateDebugSecret(context)
                                    OutlinedButton(
                                        onClick = {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(secret))
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Copia Debug Secret", color = LuxGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 1. Car Topic / Model
                item {
                    OutlinedTextField(
                        value = carTopic,
                        onValueChange = { carTopic = it },
                        label = { Text("Modello o Argomento Automobile *", fontSize = 11.sp) },
                        placeholder = { Text("es: Ferrari 488 Pista, Porsche 911 GT3 RS", color = LuxTextMuted, fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("ai_car_topic_input")
                    )
                }

                // 2. Format selector (16:9 Long vs 9:16 Shorts)
                item {
                    Text(text = "Formato Video:", color = LuxTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(ProjectFormat.LONG_VIDEO to "16:9 Video Lungo", ProjectFormat.SHORTS to "9:16 Shorts").forEach { (fmt, label) ->
                            val isSel = selectedFormat == fmt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) LuxGoldPrimary else LuxSurfaceCard)
                                    .border(1.dp, if (isSel) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedFormat = fmt
                                        durationMinutes = if (fmt == ProjectFormat.LONG_VIDEO) 8 else 1
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color(0xFF121006) else LuxTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // 3. Duration Selector
                item {
                    Text(text = "Durata Desiderata:", color = LuxTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    val durations = if (selectedFormat == ProjectFormat.LONG_VIDEO) listOf(5, 8, 12, 16) else listOf(1)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durations.forEach { d ->
                            val isSel = durationMinutes == d
                            val label = if (selectedFormat == ProjectFormat.LONG_VIDEO) "$d min (~${d * 135} parole)" else "45-60 sec"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) LuxGoldPrimary.copy(alpha = 0.2f) else LuxSurfaceCard)
                                    .border(1.dp, if (isSel) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { durationMinutes = d }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) LuxGoldPrimary else LuxTextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // 4. Content Goal
                item {
                    OutlinedTextField(
                        value = contentGoal,
                        onValueChange = { contentGoal = it },
                        label = { Text("Obiettivo del Contenuto", fontSize = 11.sp) },
                        placeholder = { Text("es: Spiegare costi reali, appassionare con la storia", color = LuxTextMuted, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 5. Automotive Category dropdown (all 9 categories)
                item {
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoria Automotive (9 aree)", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            AutomotiveCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName, fontSize = 11.sp) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 6. Narrative Tone
                item {
                    Text(text = "Tono Narrativo:", color = LuxTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    val tones = listOf("Autorevole & Analitico", "Coinvolgente & Storytelling", "Provocatorio & Diretto", "Documentaristico di Lusso")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(tones) { tone ->
                            val isSel = narrativeTone == tone
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) LuxGoldPrimary else LuxSurfaceCard)
                                    .border(1.dp, if (isSel) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(12.dp))
                                    .clickable { narrativeTone = tone }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = tone,
                                    color = if (isSel) Color(0xFF121006) else LuxTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // 7. Optional User Sources and verified information
                item {
                    OutlinedTextField(
                        value = userSources,
                        onValueChange = { userSources = it },
                        label = { Text("Eventuali Fonti o Dati Specifici (Opzionale)", fontSize = 11.sp) },
                        placeholder = { Text("es: Costo tagliando noto, svalutazione registrata, dettagli ACI", color = LuxTextMuted, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (carTopic.isNotBlank()) {
                        onGenerate(
                            AiScriptRequest(
                                carTopic = carTopic.trim(),
                                format = selectedFormat,
                                durationMinutes = durationMinutes,
                                contentGoal = contentGoal.trim(),
                                category = selectedCategory,
                                narrativeTone = narrativeTone,
                                userSourcesOrNotes = userSources.trim()
                            )
                        )
                    }
                },
                enabled = !isGenerating && carTopic.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LuxGoldPrimary,
                    contentColor = Color(0xFF121006)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_ai_generation_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color(0xFF121006), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(generationProgress ?: "Generazione con Gemini in corso...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (errorMessage != null) "Riprova Generazione" else "Genera con AI", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        },
        dismissButton = {
            if (!isGenerating) {
                OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                    Text("Annulla", color = LuxTextMuted, fontSize = 11.sp)
                }
            }
        }
    )
}

@Composable
fun VideoProjectCard(
    project: VideoProject,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusEnum = try {
        ProjectStatus.valueOf(project.status)
    } catch (_: Exception) {
        ProjectStatus.DRAFT
    }

    val statusColor = when (statusEnum) {
        ProjectStatus.IDEA -> LuxTextMuted
        ProjectStatus.DRAFT -> LuxWarningAmber
        ProjectStatus.IN_PRODUCTION -> LuxInfoBlue
        ProjectStatus.READY -> LuxGoldPrimary
        ProjectStatus.SCHEDULED -> LuxGoldAccent
        ProjectStatus.PUBLISHED -> LuxGreenGrowth
    }

    val originEnum = try {
        ProjectOrigin.valueOf(project.origin)
    } catch (_: Exception) {
        ProjectOrigin.USER_CREATED
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(LuxSurfaceCard)
            .border(1.dp, LuxGoldBorder.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Format & Origin badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (project.format == ProjectFormat.SHORTS.name) LuxYoutubeRed.copy(alpha = 0.15f) else LuxGoldPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (project.format == ProjectFormat.SHORTS.name) "9:16 Shorts" else "16:9 Long Video",
                            color = if (project.format == ProjectFormat.SHORTS.name) LuxYoutubeRed else LuxGoldPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Origin badge
                    val (originLabel, originColor) = when (originEnum) {
                        ProjectOrigin.AI_GENERATED -> "Gemini AI" to LuxGoldPrimary
                        ProjectOrigin.DEMO_SEED -> "Demo" to LuxWarningAmber
                        ProjectOrigin.USER_CREATED -> "Bozza Manuale" to LuxTextMuted
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(originColor.copy(alpha = 0.12f))
                            .border(0.5.dp, originColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (originEnum == ProjectOrigin.AI_GENERATED) {
                                Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = originColor, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(text = originLabel, color = originColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(0.5.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusEnum.displayName,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.title,
                color = LuxTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            if (project.hook.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hook: \"${project.hook}\"",
                    color = LuxTextSecondary,
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Duplicate, Delete, Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${project.estimatedDurationMinutes} min • ${project.category}",
                    color = LuxTextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Duplica progetto", tint = LuxGoldPrimary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Elimina progetto", tint = LuxYoutubeRed, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LuxSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Apri", color = LuxGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}
