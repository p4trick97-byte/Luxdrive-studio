package com.example.ui.screens.social

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MobileScreenShare
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoProject
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxGoldAccent
import com.example.ui.theme.LuxGoldBorder
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxInstaPurple
import com.example.ui.theme.LuxSurfaceBorder
import com.example.ui.theme.LuxSurfaceCard
import com.example.ui.theme.LuxSurfaceElevated
import com.example.ui.theme.LuxTextMuted
import com.example.ui.theme.LuxTextPrimary
import com.example.ui.theme.LuxTextSecondary
import com.example.ui.theme.LuxTiktokPink
import com.example.ui.theme.LuxYoutubeRed

@Composable
fun SocialDistributionScreen(
    state: SocialUiState,
    viewModel: SocialViewModel,
    onNavigateToProject: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    fun copyText(label: String, content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, content))
        viewModel.setFeedback("$label copiato negli appunti!")
    }

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

            // Strategy Header
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.MobileScreenShare, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "DISTRIBUZIONE DERIVATA MULTI-PIATTAFORMA", color = LuxGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "TikTok, Instagram Reels & YouTube Shorts", color = LuxTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "YouTube resta il fulcro della monetizzazione a lungo termine. I canali social verticali servono come imbuto di traffico per attrarre nuovi iscritti al canale principale.",
                            color = LuxTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Platform Switcher
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("TIKTOK" to "TikTok (9:16)", "INSTAGRAM" to "Reels Instagram", "SHORTS" to "YouTube Shorts").forEach { (tabKey, label) ->
                        val isSel = state.selectedPlatformTab == tabKey
                        val activeColor = when (tabKey) {
                            "TIKTOK" -> LuxTiktokPink
                            "INSTAGRAM" -> LuxInstaPurple
                            else -> LuxYoutubeRed
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) activeColor.copy(alpha = 0.2f) else LuxSurfaceCard)
                                .border(1.dp, if (isSel) activeColor else LuxSurfaceBorder, RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectPlatformTab(tabKey) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) activeColor else LuxTextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Notice about independent metadata
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(LuxSurfaceCard)
                        .border(1.dp, LuxSurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = LuxGoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tutti i contenuti sono pronti per essere incollati manualmente nelle rispettive app. Non vengono effettuate pubblicazioni automatiche senza autorizzazione.",
                            color = LuxTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // List of cards with derived content
            if (state.projectsWithSocial.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LuxSurfaceCard)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Nessun contenuto social derivato disponibile. Apri un progetto nel Video Lab per generarlo!", color = LuxTextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                items(state.projectsWithSocial) { project ->
                    val contentToShow = when (state.selectedPlatformTab) {
                        "TIKTOK" -> project.tiktokAdaptation.ifBlank { "Nessun adattamento TikTok specificato per questo progetto." }
                        "INSTAGRAM" -> project.instagramReelAdaptation.ifBlank { "Nessun adattamento Instagram Reels specificato per questo progetto." }
                        else -> project.shortsDerivedScript.ifBlank { "Nessuno script Shorts derivato specificato per questo progetto." }
                    }

                    SocialPostCard(
                        projectTitle = project.title,
                        category = project.category,
                        platform = state.selectedPlatformTab,
                        content = contentToShow,
                        onCopy = { copyText("Contenuto ${state.selectedPlatformTab}", contentToShow) },
                        onOpenProject = { onNavigateToProject(project.id) }
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
fun SocialPostCard(
    projectTitle: String,
    category: String,
    platform: String,
    content: String,
    onCopy: () -> Unit,
    onOpenProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val platformColor = when (platform) {
        "TIKTOK" -> LuxTiktokPink
        "INSTAGRAM" -> LuxInstaPurple
        else -> LuxYoutubeRed
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(LuxSurfaceCard)
            .border(1.dp, platformColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Derivato da: $category", color = LuxGoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(platformColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = platform, color = platformColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = projectTitle, color = LuxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LuxSurfaceElevated)
                    .padding(10.dp)
            ) {
                Text(text = content, color = LuxTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Apri Progetto Padre",
                    color = LuxGoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onOpenProject() }
                )

                Button(
                    onClick = onCopy,
                    colors = ButtonDefaults.buttonColors(containerColor = platformColor, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Copia Testo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
