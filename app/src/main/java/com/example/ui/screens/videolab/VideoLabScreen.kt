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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectFormat
import com.example.data.model.ProjectStatus
import com.example.data.model.VideoProject
import com.example.ui.components.LuxConfirmDialog
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

            // Action Header: Create 16:9 Long Video or 9:16 Shorts
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.createNewProject(ProjectFormat.LONG_VIDEO) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxGoldPrimary,
                            contentColor = Color(0xFF121006)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("create_long_video_button")
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Nuovo Video Lungo", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { viewModel.createNewProject(ProjectFormat.SHORTS) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxSurfaceElevated,
                            contentColor = LuxGoldAccent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("create_shorts_button")
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Nuovo Shorts (9:16)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            // Search box
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Cerca per titolo, categoria o tag...", color = LuxTextMuted, fontSize = 12.sp) },
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
                    listOf(null to "Tutti i Formati", ProjectFormat.LONG_VIDEO.name to "16:9 Long", ProjectFormat.SHORTS.name to "9:16 Shorts").forEach { (fmt, label) ->
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
                // Format & Category badge
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

                    Text(
                        text = project.category,
                        color = LuxTextMuted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
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
                    text = "${project.estimatedDurationMinutes} min stimati",
                    color = LuxTextMuted,
                    fontSize = 11.sp
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
