package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LuxGoldAccent
import com.example.ui.theme.LuxGoldBorder
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxSurfaceBorder
import com.example.ui.theme.LuxSurfaceCard
import com.example.ui.theme.LuxTextMuted
import com.example.ui.theme.LuxTextPrimary
import com.example.ui.theme.LuxTextSecondary

@Composable
fun LuxuryLineChart(
    title: String,
    subtitle: String,
    dataPoints: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = LuxGoldPrimary,
    accentGradient: Brush = Brush.verticalGradient(
        colors = listOf(LuxGoldPrimary.copy(alpha = 0.35f), Color.Transparent)
    )
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LuxSurfaceCard)
            .border(1.dp, LuxGoldBorder.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        color = LuxTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = LuxTextMuted,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "${dataPoints.lastOrNull()?.toInt() ?: 0}",
                    color = LuxGoldAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (dataPoints.size >= 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
                        val minVal = (dataPoints.minOrNull() ?: 0f).coerceAtLeast(0f)
                        val range = (maxVal - minVal).coerceAtLeast(1f)

                        val stepX = width / (dataPoints.size - 1)

                        val points = dataPoints.mapIndexed { index, value ->
                            val x = index * stepX
                            val normalizedY = (value - minVal) / range
                            val y = height - (normalizedY * (height - 30.dp.toPx())) - 15.dp.toPx()
                            Offset(x, y)
                        }

                        // Background grid lines
                        val gridLines = 3
                        for (i in 0..gridLines) {
                            val y = height * (i.toFloat() / gridLines)
                            drawLine(
                                color = LuxSurfaceBorder.copy(alpha = 0.5f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Gradient fill under the curve
                        val fillPath = Path().apply {
                            moveTo(points.first().x, height)
                            points.forEach { lineTo(it.x, it.y) }
                            lineTo(points.last().x, height)
                            close()
                        }
                        drawPath(fillPath, brush = accentGradient)

                        // Smooth line connecting points
                        val strokePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val cur = points[i]
                                val cX = (prev.x + cur.x) / 2
                                cubicTo(cX, prev.y, cX, cur.y, cur.x, cur.y)
                            }
                        }
                        drawPath(
                            path = strokePath,
                            color = lineColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw point circles
                        points.forEach { pt ->
                            drawCircle(
                                color = LuxSurfaceCard,
                                radius = 5.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = lineColor,
                                radius = 3.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // X-Axis labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    labels.forEach { label ->
                        Text(
                            text = label,
                            color = LuxTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Text(
                    text = "Dati insufficienti per il grafico (almeno 2 rilevazioni richieste)",
                    color = LuxTextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
