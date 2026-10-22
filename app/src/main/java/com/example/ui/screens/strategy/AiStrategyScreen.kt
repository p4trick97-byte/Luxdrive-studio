package com.example.ui.screens.strategy

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiStrategyProposal
import com.example.data.model.AutomotiveCategory
import com.example.data.model.ProjectFormat
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
fun AiStrategyScreen(
    state: StrategyUiState,
    viewModel: AiStrategyViewModel,
    onNavigateToVideoLabDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    LaunchedEffect(state.importedProjectId) {
        state.importedProjectId?.let { newId ->
            onNavigateToVideoLabDetail(newId)
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

            // Header card with Strategy Generator trigger
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(LuxGoldContainer)
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
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CONSULENTE STRATEGICO LUXDRIVE",
                                    color = LuxGoldPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            if (state.isGenerating) {
                                CircularProgressIndicator(
                                    color = LuxGoldPrimary,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Proposte Editoriali Esclusive Automotive",
                            color = LuxTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "L'algoritmo incrocia la retention dei tuoi video con le 9 categorie automotive per massimizzare tempo di visione e monetizzazione.",
                            color = LuxTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.generateStrategicProposals() },
                            enabled = !state.isGenerating,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuxGoldPrimary,
                                contentColor = Color(0xFF121006)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("generate_strategy_button")
                        ) {
                            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Ricalcola Strategia su Dati Canale", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Category Chips (Exact 9 automotive categories)
            item {
                Column {
                    Text(
                        text = "FILTRA PER CATEGORIA AUTOMOTIVE (9 AREE)",
                        color = LuxTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            val isAll = state.selectedCategory == null
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isAll) LuxGoldPrimary else LuxSurfaceCard)
                                    .border(1.dp, if (isAll) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(20.dp))
                                    .clickable { viewModel.selectCategory(null) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Tutte le Categorie",
                                    color = if (isAll) Color(0xFF121006) else LuxTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                        items(AutomotiveCategory.entries) { category ->
                            val isSelected = state.selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) LuxGoldPrimary else LuxSurfaceCard)
                                    .border(1.dp, if (isSelected) LuxGoldPrimary else LuxSurfaceBorder, RoundedCornerShape(20.dp))
                                    .clickable { viewModel.selectCategory(category) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = category.displayName,
                                    color = if (isSelected) Color(0xFF121006) else LuxTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Proposals list
            item {
                Text(
                    text = "IDEE PROPOSTE (${state.proposals.size})",
                    color = LuxTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(state.proposals) { proposal ->
                val isSelected = state.selectedProposal?.id == proposal.id
                ProposalSummaryCard(
                    proposal = proposal,
                    isSelected = isSelected,
                    onClick = { viewModel.selectProposal(proposal) }
                )
            }

            // Detailed view of selected proposal
            item {
                state.selectedProposal?.let { selected ->
                    ProposalDetailSection(
                        proposal = selected,
                        onImportToLab = { viewModel.importProposalToVideoLab(selected) },
                        onCopyHook = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Hook", selected.initialHook))
                        }
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
fun ProposalSummaryCard(
    proposal: AiStrategyProposal,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) LuxSurfaceElevated else LuxSurfaceCard)
            .border(
                1.dp,
                if (isSelected) LuxGoldPrimary else LuxSurfaceBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(proposal.category.badgeColorHex).copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = proposal.category.displayName,
                        color = Color(proposal.category.badgeColorHex),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Potential tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = LuxWarningAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Potenziale ${proposal.viralityPotential}",
                        color = LuxWarningAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = proposal.provisionalTitle,
                color = LuxTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = proposal.coreIdea,
                color = LuxTextMuted,
                fontSize = 11.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Formato: ${proposal.recommendedFormat.displayName} • ${proposal.suggestedDuration}",
                    color = LuxGoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isSelected) "Dettagli aperti sotto ↓" else "Tocca per espandere →",
                    color = if (isSelected) LuxGoldPrimary else LuxTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProposalDetailSection(
    proposal: AiStrategyProposal,
    onImportToLab: () -> Unit,
    onCopyHook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LuxSurfaceCard)
            .border(1.5.dp, LuxGoldBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SCHEDA TECNICA DI PRODUZIONE",
                    color = LuxGoldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Button(
                    onClick = onImportToLab,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LuxGoldPrimary,
                        contentColor = Color(0xFF121006)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("import_to_video_lab_button")
                ) {
                    Icon(imageVector = Icons.Filled.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Crea in Video Lab", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Hook iniziale box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(LuxSurfaceElevated)
                    .border(1.dp, LuxGoldBorder.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HOOK INIZIALE (PRIMI 5 SECONDI)",
                            color = LuxGoldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onCopyHook, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Copia hook", tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${proposal.initialHook}\"",
                        color = LuxTextPrimary,
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Audience & Goal
            Column {
                Text(text = "Pubblico Potenziale:", color = LuxTextMuted, fontSize = 11.sp)
                Text(text = proposal.potentialAudience, color = LuxTextPrimary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Obiettivo del Video:", color = LuxTextMuted, fontSize = 11.sp)
                Text(text = proposal.videoGoal, color = LuxTextPrimary, fontSize = 12.sp)
            }

            // Long video outline
            Column {
                Text(text = "Struttura Video Lungo:", color = LuxGoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = proposal.longVideoVersionSummary, color = LuxTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
            }

            // Secondary content ideas (Shorts, TikTok, Instagram)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(LuxDarkBackground)
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "DISTRIBUZIONE DERIVATA:", color = LuxTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    proposal.derivedShortsIdeas.forEach { shortIdea ->
                        Text(text = "• Shorts: $shortIdea", color = LuxTextPrimary, fontSize = 11.sp)
                    }
                    Text(text = "• TikTok: ${proposal.tiktokAdaptationNotes}", color = LuxTextSecondary, fontSize = 11.sp)
                    Text(text = "• Instagram Reels: ${proposal.instagramReelsAdaptationNotes}", color = LuxTextSecondary, fontSize = 11.sp)
                }
            }

            // Verification & Epistemic check (Verified facts vs Estimates vs Hypotheses)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "VERIFICA RIGOROSA DELLE FONTI (NESSUNA INVENZIONE):", color = LuxTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                // Verified facts
                proposal.verifiedFacts.forEach { fact ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = LuxGreenGrowth, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Fatto verificato: $fact", color = LuxTextPrimary, fontSize = 11.sp)
                    }
                }

                // Estimated data
                proposal.estimatedData.forEach { est ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = LuxWarningAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Dato stimato: $est", color = LuxWarningAmber, fontSize = 11.sp)
                    }
                }

                // Hypotheses
                proposal.editorialHypotheses.forEach { hyp ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Filled.HelpOutline, contentDescription = null, tint = LuxInfoBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Ipotesi editoriale: $hyp", color = LuxInfoBlue, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
