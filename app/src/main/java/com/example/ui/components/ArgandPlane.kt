package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MathUtils
import com.example.ui.theme.AngleArcColor
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ImagAxisColor
import com.example.ui.theme.RealAxisColor
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ArgandPlane(
    real: Double,
    imag: Double,
    onPointChange: (Double, Double) -> Unit,
    modifier: Modifier = Modifier,
    useJ: Boolean = false,
    interactive: Boolean = true
) {
    var manualZoomFactor by remember { mutableFloatStateOf(1f) }

    val animReal by animateFloatAsState(
        targetValue = real.toFloat(),
        animationSpec = tween(durationMillis = 200),
        label = "animReal"
    )
    val animImag by animateFloatAsState(
        targetValue = imag.toFloat(),
        animationSpec = tween(durationMillis = 200),
        label = "animImag"
    )

    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    val axisColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .testTag("argand_plane_canvas_box")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(interactive) {
                    if (!interactive) return@pointerInput
                    detectTapGestures { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val maxCoord = max(abs(real), max(abs(imag), 2.0)).toFloat() * 1.4f / manualZoomFactor
                        val scale = (size.width.coerceAtMost(size.height) / 2f) / maxCoord

                        val newRe = ((offset.x - centerX) / scale).toDouble()
                        val newIm = ((centerY - offset.y) / scale).toDouble()

                        // Snap to nearest 0.5 if very close
                        val snappedRe = if (abs(newRe - (newRe * 2).roundToInt() / 2.0) < 0.1) {
                            (newRe * 2).roundToInt() / 2.0
                        } else newRe
                        val snappedIm = if (abs(newIm - (newIm * 2).roundToInt() / 2.0) < 0.1) {
                            (newIm * 2).roundToInt() / 2.0
                        } else newIm

                        onPointChange(snappedRe, snappedIm)
                    }
                }
                .pointerInput(interactive) {
                    if (!interactive) return@pointerInput
                    detectDragGestures { change, _ ->
                        change.consume()
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val maxCoord = max(abs(real), max(abs(imag), 2.0)).toFloat() * 1.4f / manualZoomFactor
                        val scale = (size.width.coerceAtMost(size.height) / 2f) / maxCoord

                        val newRe = ((change.position.x - centerX) / scale).toDouble()
                        val newIm = ((centerY - change.position.y) / scale).toDouble()

                        onPointChange(newRe, newIm)
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            val rMagnitude = sqrt(animReal * animReal + animImag * animImag)
            val maxCoord = max(abs(animReal), max(abs(animImag), 2.0f)) * 1.35f / manualZoomFactor
            val minDimension = width.coerceAtMost(height)
            val scale = (minDimension / 2f) / maxCoord

            // 1. Draw Grid Lines & Concentric circles
            drawGrid(centerX, centerY, width, height, scale, gridColor)

            // 2. Draw Main Axes
            drawLine(
                color = axisColor,
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = axisColor,
                start = Offset(centerX, height),
                end = Offset(centerX, 0f),
                strokeWidth = 2.dp.toPx()
            )

            // Axis arrows
            val arrowSize = 12f
            drawPath(
                path = Path().apply {
                    moveTo(width, centerY)
                    lineTo(width - arrowSize, centerY - arrowSize / 2)
                    lineTo(width - arrowSize, centerY + arrowSize / 2)
                    close()
                },
                color = axisColor
            )
            drawPath(
                path = Path().apply {
                    moveTo(centerX, 0f)
                    lineTo(centerX - arrowSize / 2, arrowSize)
                    lineTo(centerX + arrowSize / 2, arrowSize)
                    close()
                },
                color = axisColor
            )

            // Vector target position in canvas coordinates
            val pointX = centerX + animReal * scale
            val pointY = centerY - animImag * scale

            // 3. Projections to axes (dashed lines)
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            // Vertical projection to Re axis
            drawLine(
                color = RealAxisColor.copy(alpha = 0.7f),
                start = Offset(pointX, pointY),
                end = Offset(pointX, centerY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dashEffect
            )

            // Horizontal projection to Im axis
            drawLine(
                color = ImagAxisColor.copy(alpha = 0.7f),
                start = Offset(pointX, pointY),
                end = Offset(centerX, pointY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = dashEffect
            )

            // 4. Dashed circle of radius r
            if (rMagnitude > 0.05f) {
                drawCircle(
                    color = CyanAccent.copy(alpha = 0.25f),
                    center = Offset(centerX, centerY),
                    radius = rMagnitude * scale,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                )
            }

            // 5. Angle Arc
            if (rMagnitude > 0.1f) {
                val angleRad = atan2(animImag, animReal)
                val angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
                val arcRadius = (32.dp.toPx()).coerceAtMost((rMagnitude * scale * 0.65f).coerceAtLeast(18.dp.toPx()))

                // Canvas drawArc starts 0 at positive X axis (clockwise is positive angle)
                // In math, counter-clockwise is positive, so sweepAngle = -angleDeg
                val sweep = -angleDeg
                drawArc(
                    color = AngleArcColor,
                    startAngle = 0f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(centerX - arcRadius, centerY - arcRadius),
                    size = Size(arcRadius * 2, arcRadius * 2),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 6. Vector Arrow
            if (rMagnitude > 0.01f) {
                // Vector line
                drawLine(
                    color = CyanAccent,
                    start = Offset(centerX, centerY),
                    end = Offset(pointX, pointY),
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Vector head arrow
                val angle = atan2(pointY - centerY, pointX - centerX)
                val headLength = 18f
                val headAngle = PI.toFloat() / 6f // 30 degrees

                val arrowP1 = Offset(
                    pointX - headLength * cos(angle - headAngle),
                    pointY - headLength * sin(angle - headAngle)
                )
                val arrowP2 = Offset(
                    pointX - headLength * cos(angle + headAngle),
                    pointY - headLength * sin(angle + headAngle)
                )

                drawPath(
                    path = Path().apply {
                        moveTo(pointX, pointY)
                        lineTo(arrowP1.x, arrowP1.y)
                        lineTo(arrowP2.x, arrowP2.y)
                        close()
                    },
                    color = CyanGlow
                )
            }

            // 7. Point Handle (Draggable Point)
            drawCircle(
                color = CyanAccent.copy(alpha = 0.35f),
                radius = 14.dp.toPx(),
                center = Offset(pointX, pointY)
            )
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(pointX, pointY)
            )
            drawCircle(
                color = CyanAccent,
                radius = 3.5.dp.toPx(),
                center = Offset(pointX, pointY)
            )

            // 8. Labels via Android Native Canvas Paint
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    color = onSurfaceColor.hashCode()
                    textSize = 12.sp.toPx()
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }

                // Re label
                drawText("Re", width - 30f, centerY + 32f, textPaint)

                // Im label
                val unit = if (useJ) "j" else "i"
                drawText("Im ($unit)", centerX + 12f, 28f, textPaint)

                // Origin 0
                textPaint.color = axisColor.hashCode()
                textPaint.textSize = 10.sp.toPx()
                drawText("0", centerX - 18f, centerY + 18f, textPaint)
            }
        }

        // Overlay Badges & Controls
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { manualZoomFactor = (manualZoomFactor * 1.3f).coerceAtMost(3.0f) },
                modifier = Modifier.testTag("zoom_in_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Увеличить масштаб",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { manualZoomFactor = (manualZoomFactor / 1.3f).coerceAtLeast(0.4f) },
                modifier = Modifier.testTag("zoom_out_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Уменьшить масштаб",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { manualZoomFactor = 1.0f },
                modifier = Modifier.testTag("zoom_reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Сбросить масштаб",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Coordinate readout chip at bottom left
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            tonalElevation = 2.dp
        ) {
            val rStr = MathUtils.formatDouble(sqrt(real * real + imag * imag), 2)
            val degStr = MathUtils.formatDouble(Math.toDegrees(atan2(imag, real)), 1)
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "r = $rStr  |  φ = $degStr°",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun DrawScope.drawGrid(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    scale: Float,
    gridColor: Color
) {
    // Determine grid step in coordinate units (1, 2, 5, 10...)
    val minPixelSpacing = 50f
    val roughStep = minPixelSpacing / scale
    val step = when {
        roughStep <= 1f -> 1f
        roughStep <= 2f -> 2f
        roughStep <= 5f -> 5f
        roughStep <= 10f -> 10f
        else -> 20f
    }

    val pixelStep = step * scale

    // Vertical grid lines
    var x = centerX + pixelStep
    while (x < width) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
        x += pixelStep
    }
    x = centerX - pixelStep
    while (x > 0) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
        x -= pixelStep
    }

    // Horizontal grid lines
    var y = centerY + pixelStep
    while (y < height) {
        drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
        y += pixelStep
    }
    y = centerY - pixelStep
    while (y > 0) {
        drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
        y -= pixelStep
    }
}
