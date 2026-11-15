package com.pixelroot.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PixelRootEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 88.dp
) {
    val containerColor = Color(0xFFFFFFFF)
    val inkColor = Color(0xFF0B0B0B)
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.72f)) {
            val s = this.size.minDimension
            val strokeW = s * 0.082f
            val halfStroke = strokeW / 2f
            val corner = s * 0.10f

            // Outer rounded square frame
            drawRoundRect(
                color = inkColor,
                topLeft = Offset(halfStroke, halfStroke),
                size = Size(s - strokeW, s - strokeW),
                cornerRadius = CornerRadius(corner, corner),
                style = Stroke(width = strokeW)
            )

            // Inner grid bounds with white gap inside frame
            val innerStart = s * 0.13f
            val innerEnd = s * 0.87f
            val innerSpan = innerEnd - innerStart
            val barThickness = s * 0.11f
            val barHalf = barThickness / 2f
            val mid = s / 2f
            val quadGap = s * 0.045f

            // Vertical crosshair bar
            drawRect(
                color = inkColor,
                topLeft = Offset(mid - barHalf, innerStart),
                size = Size(barThickness, innerSpan)
            )

            // Horizontal crosshair bar
            drawRect(
                color = inkColor,
                topLeft = Offset(innerStart, mid - barHalf),
                size = Size(innerSpan, barThickness)
            )

            // Top-right solid square
            val trLeft = mid + barHalf + quadGap
            val trBottom = mid - barHalf - quadGap
            drawRect(
                color = inkColor,
                topLeft = Offset(trLeft, innerStart),
                size = Size(innerEnd - trLeft, trBottom - innerStart)
            )

            // Bottom-left solid square
            val blRight = mid - barHalf - quadGap
            val blTop = mid + barHalf + quadGap
            drawRect(
                color = inkColor,
                topLeft = Offset(innerStart, blTop),
                size = Size(blRight - innerStart, innerEnd - blTop)
            )
        }
    }
}

@Composable
fun SuManagerBadge(
    isNext: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(11.dp))
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(11.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.58f)) {
            val ink = Color(0xFF0B0B0B)
            if (!isNext) {
                val half = this.size.width / 2f
                drawRect(ink, Offset.Zero, Size(half, half))
                drawRect(ink, Offset(half, half), Size(half, half))
                drawRect(ink, Offset.Zero, this.size, style = Stroke(width = 1.8.dp.toPx()))
            } else {
                val radius = this.size.minDimension / 2f
                drawCircle(color = ink, radius = radius)
                drawArc(
                    color = Color.White,
                    startAngle = -90f,
                    sweepAngle = 180f,
                    useCenter = true
                )
                drawCircle(
                    color = ink,
                    radius = radius / 2f,
                    center = Offset(center.x, center.y - radius / 2f)
                )
                drawCircle(
                    color = Color.White,
                    radius = radius / 2f,
                    center = Offset(center.x, center.y + radius / 2f)
                )
                drawCircle(
                    color = Color.White,
                    radius = radius / 6f,
                    center = Offset(center.x, center.y - radius / 2f)
                )
                drawCircle(
                    color = ink,
                    radius = radius / 6f,
                    center = Offset(center.x, center.y + radius / 2f)
                )
                drawCircle(
                    color = ink,
                    radius = radius,
                    style = Stroke(width = 1.6.dp.toPx())
                )
            }
        }
    }
}
