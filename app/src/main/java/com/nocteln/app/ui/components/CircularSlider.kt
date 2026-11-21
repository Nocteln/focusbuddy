package com.nocteln.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private fun isOnRing(pos: Offset, size: IntSize, radius: Float, tolerance: Float): Boolean {
    val dx = pos.x - size.width / 2f
    val dy = pos.y - size.height / 2f
    val distance = hypot(dx, dy)
    return abs(distance - radius) <= tolerance
}

private fun fractionAt(pos: Offset, size: IntSize): Float {
    val dx = pos.x - size.width / 2f
    val dy = pos.y - size.height / 2f
    var angle = atan2(dx, -dy)
    if (angle < 0) angle += (2 * PI).toFloat()
    return angle / (2 * PI).toFloat()
}

@Composable
fun CircularSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 12.dp,
    enabled: Boolean = true,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    disabledColor: Color = MaterialTheme.colorScheme.tertiary
) {
    // Garantit que les gestes utilisent toujours la valeur la plus récente
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    val fillColor: Color = if (enabled) progressColor else disabledColor

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                val radius = minOf(size.width, size.height) / 2f - strokeWidth.toPx()
                val tolerance = strokeWidth.toPx() * 2
                var dragging = false

                detectDragGestures(
                    onDragStart = { pos ->
                        dragging = isOnRing(pos, size, radius, tolerance)
                        if (dragging) currentOnValueChange(fractionAt(pos, size))
                    },
                    onDrag = { change, _ ->
                        if (dragging)
                        {
                            val new = fractionAt(change.position, size)

                            val safe = when {
                                new - currentValue > 0.5f -> 0f
                                currentValue - new > 0.5f -> 1f
                                else -> new
                            }
                            currentOnValueChange(safe)
                        }
                    }
                )
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput

                val radius = minOf(size.width, size.height) / 2f - strokeWidth.toPx()
                val tolerance = strokeWidth.toPx() * 2

                detectTapGestures(
                    onTap = { pos ->
                        if (isOnRing(pos, size, radius, tolerance)) {
                            currentOnValueChange(fractionAt(pos, size))
                        }
                    }
                )
            }
    ) {
        val stroke = strokeWidth.toPx()
        val thumbRadius = stroke
        val radius = size.minDimension / 2 - thumbRadius

        // 1. Le cercle de fond
        drawCircle(trackColor, radius = radius, style = Stroke(stroke))

        // 2. L'arc rempli, qui part du haut (-90°)
        drawArc(
            color = fillColor,
            startAngle = -90f,
            sweepAngle = 360f * value,
            useCenter = false,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(stroke, cap = StrokeCap.Round)
        )

        // 3. La pastille au bout de l'arc
        if (enabled) {
            val angle = Math.toRadians(360.0 * value - 90)
            val thumbPos = center + Offset(
                (cos(angle) * radius).toFloat(),
                (sin(angle) * radius).toFloat()
            )
            drawCircle(fillColor, radius = thumbRadius, center = thumbPos)
        }
    }
}