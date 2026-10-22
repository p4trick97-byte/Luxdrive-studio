package com.example.ui.screens.hub

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MobileScreenShare
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxGoldAccent
import com.example.ui.theme.LuxGoldBorder
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGreenGrowth
import com.example.ui.theme.LuxInfoBlue
import com.example.ui.theme.LuxSurfaceBorder
import com.example.ui.theme.LuxSurfaceCard
import com.example.ui.theme.LuxTextMuted
import com.example.ui.theme.LuxTextPrimary
import com.example.ui.theme.LuxTextSecondary
import com.example.ui.theme.LuxTiktokPink

@Composable
fun StudioHubScreen(
    onNavigateToCalendar: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToSocial: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LuxDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.GridView, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Studio Hub LuxDrive", color = LuxTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Centro di controllo moduli e distribuzione", color = LuxTextMuted, fontSize = 11.sp)
                    }
                }
            }

            item {
                HubSectionCard(
                    title = "Calendario Editoriale",
                    subtitle = "Timeline uscite programmate, stati di lavorazione e scadenze",
                    icon = Icons.Filled.CalendarMonth,
                    accentColor = LuxGreenGrowth,
                    testTag = "hub_calendar_card",
                    onClick = onNavigateToCalendar
                )
            }

            item {
                HubSectionCard(
                    title = "Archivio Contenuti",
                    subtitle = "Ricerca avanzata per categoria, bozze, video storici e metriche",
                    icon = Icons.Filled.Archive,
                    accentColor = LuxInfoBlue,
                    testTag = "hub_archive_card",
                    onClick = onNavigateToArchive
                )
            }

            item {
                HubSectionCard(
                    title = "Altri Social (Distribuzione)",
                    subtitle = "Adattamenti 9:16 per TikTok e Instagram Reels collegati ai progetti",
                    icon = Icons.Filled.MobileScreenShare,
                    accentColor = LuxTiktokPink,
                    testTag = "hub_social_card",
                    onClick = onNavigateToSocial
                )
            }

            item {
                HubSectionCard(
                    title = "Impostazioni & Future Integrazioni",
                    subtitle = "Soglie monetizzazione YPP, YouTube API OAuth, Gemini AI e backup",
                    icon = Icons.Filled.Settings,
                    accentColor = LuxGoldAccent,
                    testTag = "hub_settings_card",
                    onClick = onNavigateToSettings
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun HubSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(LuxSurfaceCard)
            .border(1.dp, LuxGoldBorder.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = LuxTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = LuxTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = LuxGoldPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
