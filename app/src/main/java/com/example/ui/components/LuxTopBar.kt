package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxGoldBorder
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGreenGrowth
import com.example.ui.theme.LuxSurfaceCard
import com.example.ui.theme.LuxTextMuted
import com.example.ui.theme.LuxTextPrimary
import com.example.ui.theme.LuxTextSecondary
import com.example.ui.theme.LuxWarningAmber

@Composable
fun LuxTopBar(
    title: String,
    subtitle: String = "LUXDRIVE AUTOMOTIVE STUDIO",
    isSyncing: Boolean = false,
    isConnected: Boolean = false,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(LuxDarkBackground)
            .border(width = 1.dp, color = LuxSurfaceCard, shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LuxDrive Logo emblem badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LuxSurfaceCard)
                    .border(1.dp, LuxGoldBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_luxdrive_logo),
                    contentDescription = "LuxDrive Logo",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subtitle,
                    color = LuxGoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = title,
                    color = LuxTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Connection status pill
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isConnected) LuxGreenGrowth.copy(alpha = 0.15f) else LuxWarningAmber.copy(alpha = 0.15f))
                    .border(
                        0.5.dp,
                        if (isConnected) LuxGreenGrowth.copy(alpha = 0.5f) else LuxWarningAmber.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isConnected) Icons.Outlined.CheckCircle else Icons.Filled.Warning,
                        contentDescription = if (isConnected) "Connesso" else "Demo",
                        tint = if (isConnected) LuxGreenGrowth else LuxWarningAmber,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isConnected) "Live" else "Demo Locale",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isConnected) LuxGreenGrowth else LuxWarningAmber
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSyncClick,
                enabled = !isSyncing,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("top_bar_sync_button")
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = LuxGoldPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Sincronizza metriche",
                        tint = LuxGoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
