package com.example.ui.screens.videolab

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Smartphone
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLabDetailScreen(
    projectId: Long,
    viewModel: VideoLabViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    val state = viewModel.state.value
    val project = state.currentEditingProject

    if (project == null) {
        Box(
            modifier = modifier.fillMaxSize().background(LuxDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LuxGoldPrimary)
        }
        return
    }

    // Local form state
    var title by remember(project.id) { mutableStateOf(project.title) }
    var category by remember(project.id) { mutableStateOf(project.category) }
    var format by remember(project.id) { mutableStateOf(project.format) }
    var status by remember(project.id) { mutableStateOf(project.status) }
    var hook by remember(project.id) { mutableStateOf(project.hook) }
    var researchAndSources by remember(project.id) { mutableStateOf(project.researchAndSources) }
    var fullScript by remember(project.id) { mutableStateOf(project.fullScript) }
    var voiceOverNotes by remember(project.id) { mutableStateOf(project.voiceOverNotes) }
    var capcutNotes by remember(project.id) { mutableStateOf(project.capcutEditingNotes) }
    var youtubeTitleAlt by remember(project.id) { mutableStateOf(project.youtubeTitleAlternative) }
    var description by remember(project.id) { mutableStateOf(project.description) }
    var chaptersText by remember(project.id) { mutableStateOf(project.chaptersText) }
    var tags by remember(project.id) { mutableStateOf(project.tags) }
    var thumbnailConcept by remember(project.id) { mutableStateOf(project.thumbnailConcept) }
    var shortsDerivedScript by remember(project.id) { mutableStateOf(project.shortsDerivedScript) }
    var tiktokAdaptation by remember(project.id) { mutableStateOf(project.tiktokAdaptation) }
    var durationMinutesText by remember(project.id) { mutableStateOf(project.estimatedDurationMinutes.toString()) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Script & Voice", "CapCut & Story", "YouTube Info", "Shorts & Social")

    var showDeleteConfirm by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    Box(modifier = modifier.fillMaxSize().background(LuxDarkBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Action Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LuxSurfaceCard)
                    .border(1.dp, LuxSurfaceBorder)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = LuxTextPrimary)
                        }
                        Text(
                            text = "Editor Studio Video",
                            color = LuxTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Elimina", tint = LuxYoutubeRed)
                        }

                        Button(
                            onClick = {
                                val updated = project.copy(
                                    title = title,
                                    category = category,
                                    format = format,
                                    status = status,
                                    hook = hook,
                                    researchAndSources = researchAndSources,
                                    fullScript = fullScript,
                                    voiceOverNotes = voiceOverNotes,
                                    capcutEditingNotes = capcutNotes,
                                    estimatedDurationMinutes = durationMinutesText.toIntOrNull() ?: 10,
                                    youtubeTitleAlternative = youtubeTitleAlt,
                                    description = description,
                                    chaptersText = chaptersText,
                                    tags = tags,
                                    thumbnailConcept = thumbnailConcept,
                                    shortsDerivedScript = shortsDerivedScript,
                                    tiktokAdaptation = tiktokAdaptation
                                )
                                viewModel.saveProject(updated)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary, contentColor = Color(0xFF121006)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("save_project_button")
                        ) {
                            Icon(imageVector = Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Salva", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Tabs for organized editing
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = LuxDarkBackground,
                contentColor = LuxGoldPrimary
            ) {
                tabTitles.forEachIndexed { index, titleText ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = titleText,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) LuxGoldPrimary else LuxTextMuted
                            )
                        }
                    )
                }
            }

            // Body content according to tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { Spacer(modifier = Modifier.height(6.dp)) }

                // Essential Title & Status (Always visible)
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titolo Principale del Progetto", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("project_title_input")
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Status selector
                        var statusExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = statusExpanded,
                            onExpandedChange = { statusExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = status,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Stato Lavorazione", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = statusExpanded,
                                onDismissRequest = { statusExpanded = false }
                            ) {
                                ProjectStatus.entries.forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text(st.displayName, fontSize = 12.sp) },
                                        onClick = {
                                            status = st.name
                                            statusExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Duration minutes
                        OutlinedTextField(
                            value = durationMinutesText,
                            onValueChange = { durationMinutesText = it },
                            label = { Text("Durata (min)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(110.dp)
                        )
                    }
                }

                // TAB 0: SCRIPT & VOICE-OVER & RESEARCH
                if (selectedTab == 0) {
                    item {
                        OutlinedTextField(
                            value = hook,
                            onValueChange = { hook = it },
                            label = { Text("Hook Iniziale (0-5 secondi)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "SCRIPT COMPLETO IN ITALIANO", color = LuxGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { copyToClipboard("Script", fullScript) }) {
                                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Copia Script", tint = LuxGoldPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = fullScript,
                            onValueChange = { fullScript = it },
                            placeholder = { Text("Scrivi lo script dettagliato della narrazione...") },
                            modifier = Modifier.fillMaxWidth().height(260.dp),
                            minLines = 10
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = voiceOverNotes,
                            onValueChange = { voiceOverNotes = it },
                            label = { Text("Note di Registrazione Voice-Over (Tono, pause, enfasi)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = researchAndSources,
                            onValueChange = { researchAndSources = it },
                            label = { Text("Fonti Verificate e Dati Tecnici ACI/Costruttore", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }

                // TAB 1: CAPCUT EDITING DA SMARTPHONE & STORYBOARD
                if (selectedTab == 1) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(LuxSurfaceCard)
                                .border(1.dp, LuxGoldBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Smartphone, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "Guida al Montaggio CapCut da Smartphone", color = LuxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Istruzioni ottimizzate per Samsung Galaxy S23 e workflow mobile.", color = LuxTextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "NOTE DI MONTAGGIO & STORYBOARD", color = LuxGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { copyToClipboard("Istruzioni CapCut", capcutNotes) }) {
                                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Copia CapCut", tint = LuxGoldPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = capcutNotes,
                            onValueChange = { capcutNotes = it },
                            placeholder = { Text("Istruzioni scena per scena, effetti, zoom, musica...") },
                            modifier = Modifier.fillMaxWidth().height(320.dp),
                            minLines = 12
                        )
                    }
                }

                // TAB 2: YOUTUBE METADATA, CAPITOLI E MINIATURA
                if (selectedTab == 2) {
                    item {
                        OutlinedTextField(
                            value = youtubeTitleAlt,
                            onValueChange = { youtubeTitleAlt = it },
                            label = { Text("Titolo YouTube Alternativo (A/B Test)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descrizione Completa Video", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(140.dp),
                            minLines = 4
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = chaptersText,
                            onValueChange = { chaptersText = it },
                            label = { Text("Capitoli con Timestamp (es: 0:00 Intro\\n1:15 Tasse)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            minLines = 4
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            label = { Text("Tag ed Hashtag (#LuxDrive #Supercar)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = thumbnailConcept,
                            onValueChange = { thumbnailConcept = it },
                            label = { Text("Concept Grafico Miniatura (Auto, colori, testo bold)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }

                // TAB 3: SHORTS & DERIVATI SOCIAL (TikTok / Instagram)
                if (selectedTab == 3) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(LuxDarkBackground)
                                .border(1.dp, LuxSurfaceBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Ogni video lungo può generare uno o più Shorts verticali da ripubblicare su TikTok e Instagram Reels per convogliare traffico al canale.",
                                color = LuxTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = shortsDerivedScript,
                            onValueChange = { shortsDerivedScript = it },
                            label = { Text("Versione Shorts 9:16 (Script rapido 30-50s)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(140.dp),
                            minLines = 5
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = tiktokAdaptation,
                            onValueChange = { tiktokAdaptation = it },
                            label = { Text("Adattamento TikTok (Hook virale + call to action)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = project.instagramReelAdaptation,
                            onValueChange = { /* read-only or customizable */ },
                            label = { Text("Adattamento Instagram Reels (Estetica e carosello)", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    LuxConfirmDialog(
        show = showDeleteConfirm,
        title = "Eliminare definitivamente?",
        message = "Questa operazione cancellerà il progetto corrente da LuxDrive.",
        onConfirm = {
            viewModel.deleteProject(project.id)
            showDeleteConfirm = false
            onNavigateBack()
        },
        onDismiss = { showDeleteConfirm = false }
    )
}
