package com.example.ui.screens.archive

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AutomotiveCategory
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
fun ArchiveScreen(
    state: ArchiveUiState,
    viewModel: ArchiveViewModel,
    onNavigateToProject: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var itemToDeleteId by remember { mutableStateOf<Long?>(null) }

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Archive header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Archive, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Archivio Generale Contenuti", color = LuxTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Tutti i video lunghi, Shorts e derivati social registrati", color = LuxTextMuted, fontSize = 11.sp)
                    }
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Cerca nell'archivio...", color = LuxTextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = LuxGoldPrimary) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Category filter chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        val isAll = state.selectedCategory == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isAll) LuxGoldPrimary else LuxSurfaceCard)
                                .border(1.dp, if (isAll) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(14.dp))
                                .clickable { viewModel.selectCategory(null) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Tutte", color = if (isAll) Color(0xFF121006) else LuxTextSecondary, fontSize = 11.sp, fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                    items(AutomotiveCategory.entries) { cat ->
                        val isSel = state.selectedCategory == cat.displayName
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSel) LuxGoldPrimary else LuxSurfaceCard)
                                .border(1.dp, if (isSel) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(14.dp))
                                .clickable { viewModel.selectCategory(cat.displayName) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = cat.displayName, color = if (isSel) Color(0xFF121006) else LuxTextSecondary, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            // List of items
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
                        Text(text = "Nessun contenuto corrisponde alla ricerca.", color = LuxTextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(state.projects) { project ->
                    ArchiveProjectItem(
                        project = project,
                        onClick = { onNavigateToProject(project.id) },
                        onDuplicate = { viewModel.duplicateProject(project.id) },
                        onDelete = { itemToDeleteId = project.id }
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

    LuxConfirmDialog(
        show = itemToDeleteId != null,
        title = "Rimuovere dall'archivio?",
        message = "Questa operazione eliminerà il video e tutti i relativi contenuti derivati.",
        onConfirm = {
            itemToDeleteId?.let { viewModel.deleteProject(it) }
            itemToDeleteId = null
        },
        onDismiss = { itemToDeleteId = null }
    )
}

@Composable
fun ArchiveProjectItem(
    project: VideoProject,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = project.category, color = LuxGoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LuxSurfaceElevated)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = project.status, color = LuxTextMuted, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = project.title, color = LuxTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            // Connected derived items indicators (Shorts, TikTok, Reels)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LuxDarkBackground)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(12.dp))
                Text(text = "Derivati:", color = LuxTextMuted, fontSize = 10.sp)
                Text(text = if (project.shortsDerivedScript.isNotBlank()) "✓ Shorts" else "- Shorts", color = if (project.shortsDerivedScript.isNotBlank()) LuxGreenGrowth else LuxTextMuted, fontSize = 10.sp)
                Text(text = if (project.tiktokAdaptation.isNotBlank()) "✓ TikTok" else "- TikTok", color = if (project.tiktokAdaptation.isNotBlank()) LuxGreenGrowth else LuxTextMuted, fontSize = 10.sp)
                Text(text = if (project.instagramReelAdaptation.isNotBlank()) "✓ Reels" else "- Reels", color = if (project.instagramReelAdaptation.isNotBlank()) LuxGreenGrowth else LuxTextMuted, fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (project.viewsCount > 0) {
                    Text(text = "${project.viewsCount} visualizzazioni • ${project.watchHours} ore", color = LuxTextSecondary, fontSize = 11.sp)
                } else {
                    Text(text = "Non ancora pubblicato", color = LuxTextMuted, fontSize = 11.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Duplica", tint = LuxGoldPrimary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Elimina", tint = LuxYoutubeRed, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
