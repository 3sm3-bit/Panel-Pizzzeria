package com.pizzzeria.panelpizzzeria.utils


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

fun Modifier.outerBeamEffect(
    enabled: Boolean,
    cornerRadius: Dp = 20.dp,
    beamStrokeWidth: Dp = 2.dp,
    glowStrokeWidth: Dp = 6.dp,
    beamLength: Dp = 42.dp,
    tailColor: Color = Color(0xFF10B981),
    midColor: Color = Color(0xFF6EE7B7),
    headColor: Color = Color(0xFFFFFFFF),
    durationMillis: Int = 2200
): Modifier = composed {
    if (!enabled) return@composed this

    val transition = rememberInfiniteTransition(label = "outer_beam_effect")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_beam_effect_progress"
    )

    this.drawWithCache {
        val beamPx = beamStrokeWidth.toPx()
        val glowPx = glowStrokeWidth.toPx()
        val outerOffset = beamPx / 2f + 1.dp.toPx()
        val r = min(
            cornerRadius.toPx() + outerOffset,
            min(size.width, size.height) / 2f + outerOffset
        ).coerceAtLeast(0f)

        val beamPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = -outerOffset,
                    top = -outerOffset,
                    right = size.width + outerOffset,
                    bottom = size.height + outerOffset,
                    cornerRadius = CornerRadius(r, r)
                )
            )
        }

        val pathMeasure = PathMeasure().apply { setPath(beamPath, forceClosed = true) }
        val totalLength = pathMeasure.length
        val segLength = min(beamLength.toPx(), totalLength * 0.35f)

        onDrawWithContent {
            if (totalLength > 0f && segLength > 0f) {
                val headDist = progress * totalLength
                val tailDist = headDist - segLength

                val segment = Path()
                if (tailDist >= 0f) {
                    pathMeasure.getSegment(tailDist, headDist, segment, startWithMoveTo = true)
                } else {
                    pathMeasure.getSegment(totalLength + tailDist, totalLength, segment, startWithMoveTo = true)
                    pathMeasure.getSegment(0f, headDist, segment, startWithMoveTo = true)
                }

                val headPos = pathMeasure.getPosition(headDist)
                val tailPos = pathMeasure.getPosition(
                    if (tailDist >= 0f) tailDist else totalLength + tailDist
                )

                // 1. Halo exterior suave
                drawPath(
                    path = segment,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            midColor.copy(alpha = 0.25f),
                            headColor.copy(alpha = 0.40f)
                        ),
                        start = tailPos,
                        end = headPos
                    ),
                    style = Stroke(
                        width = glowPx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 2. Destello principal con 3 colores
                drawPath(
                    path = segment,
                    brush = Brush.linearGradient(
                        0.0f to Color.Transparent,
                        0.35f to tailColor,
                        0.75f to midColor,
                        1.0f to headColor,
                        start = tailPos,
                        end = headPos
                    ),
                    style = Stroke(
                        width = beamPx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Dibuja el botón encima sin alterarlo ni difuminarlo
            drawContent()
        }
    }
}

fun Modifier.outerBeamSegment(
    cornerRadius: Dp = 20.dp,
    strokeWidth: Dp = 2.dp,
    beamLength: Dp = 42.dp,
    tailColor: Color = Color(0xFF10B981),
    midColor: Color = Color(0xFF6EE7B7),
    headColor: Color = Color(0xFFFFFFFF),
    durationMillis: Int = 2200
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "beam_seg")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "beam_seg_progress"
    )

    this.drawWithCache {
        val offsetPx = strokeWidth.toPx() / 2f + 1.dp.toPx()
        val r = min(
            cornerRadius.toPx() + offsetPx,
            min(size.width, size.height) / 2f + offsetPx
        ).coerceAtLeast(0f)

        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = -offsetPx,
                    top = -offsetPx,
                    right = size.width + offsetPx,
                    bottom = size.height + offsetPx,
                    cornerRadius = CornerRadius(r, r)
                )
            )
        }
        val measure = PathMeasure().apply { setPath(path, forceClosed = true) }
        val totalLen = measure.length
        val segLen = min(beamLength.toPx(), totalLen * 0.35f)

        onDrawBehind {
            if (totalLen <= 0f || segLen <= 0f) return@onDrawBehind

            val headDist = progress * totalLen
            val tailDist = headDist - segLen

            val seg = Path()
            if (tailDist >= 0f) {
                measure.getSegment(tailDist, headDist, seg, startWithMoveTo = true)
            } else {
                measure.getSegment(totalLen + tailDist, totalLen, seg, startWithMoveTo = true)
                measure.getSegment(0f, headDist, seg, startWithMoveTo = true)
            }

            val headPos = measure.getPosition(headDist)
            val tailPos = measure.getPosition(
                if (tailDist >= 0f) tailDist else totalLen + tailDist
            )

            drawPath(
                path = seg,
                brush = Brush.linearGradient(
                    0.0f to Color.Transparent,
                    0.35f to tailColor,
                    0.75f to midColor,
                    1.0f to headColor,
                    start = tailPos,
                    end = headPos
                ),
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}