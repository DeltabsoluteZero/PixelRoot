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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Signature PixelRoot / DFRoot 2x2 checkered emblem rendered in crisp Material 3 Monochrome.
 */
@Composable
fun PixelRootEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 84.dp
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
        Canvas(modifier = Modifier.size(size * 0.56f)) {
            val w = this.size.width
            val h = this.size.height
            val halfW = w / 2f
            val halfH = h / 2f

            // Top-left dark cell
            drawRect(
                color = inkColor,
                topLeft = Offset(0f, 0f),
                size = Size(halfW, halfH)
            )
            // Bottom-right dark cell
            drawRect(
                color = inkColor,
                topLeft = Offset(halfW, halfH),
                size = Size(halfW, halfH)
            )
            // Outer crisp frame
            drawRect(
                color = inkColor,
                topLeft = Offset.Zero,
                size = this.size,
                style = Stroke(width = 2.6.dp.toPx())
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
                // KernelSU 2x2 grid badge
                val half = this.size.width / 2f
                drawRect(ink, Offset.Zero, Size(half, half))
                drawRect(ink, Offset(half, half), Size(half, half))
                drawRect(ink, Offset.Zero, this.size, style = Stroke(width = 1.8.dp.toPx()))
            } else {
                // KernelSU-Next monochrome yin-yang / split circle badge
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
