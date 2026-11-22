package com.pizzzeria.panelpizzzeria.utils


import android.graphics.BlurMaskFilter
import android.os.Build
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
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

fun Modifier.outerBeamEffect(
    enabled: Boolean,
    cornerRadius: Dp = 20.dp,
    beamStrokeWidth: Dp = 2.dp,
    glowStrokeWidth: Dp = 8.dp,
    beamLength: Dp = 52.dp,
    tailColor: Color = green_600,
    midColor: Color = Color(0xFF6EE7B7),
    headColor: Color = Color(0xFFFFFFFF),
    durationMillis: Int = 2200,
    shadowRadius: Dp = 12.dp
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
        // El recorrido va sobre el borde del botón (hacia adentro la mitad del grosor del trazo)
        val inset = beamPx / 2f
        val r = min(
            cornerRadius.toPx() - inset,
            min(size.width, size.height) / 2f - inset
        ).coerceAtLeast(0f)

        val beamPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = inset,
                    top = inset,
                    right = size.width - inset,
                    bottom = size.height - inset,
                    cornerRadius = CornerRadius(r, r)
                )
            )
        }

        val pathMeasure = PathMeasure().apply { setPath(beamPath, forceClosed = true) }
        val totalLength = pathMeasure.length
        val segLength = min(beamLength.toPx(), totalLength * 0.35f)

        // Brillo difuminado (BlurMaskFilter funciona con aceleración por hardware desde API 28)
        val supportsBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        val shadowPx = shadowRadius.toPx()
        fun blurPaint(radius: Float, width: Float) =
            android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = width
                strokeCap = android.graphics.Paint.Cap.ROUND
                strokeJoin = android.graphics.Paint.Join.ROUND
                maskFilter = BlurMaskFilter(radius.coerceAtLeast(1f), BlurMaskFilter.Blur.NORMAL)
            }
        // Sombra amplia: se dibuja detrás del botón, así solo se ve hacia afuera
        val outerShadowPaint = if (supportsBlur) blurPaint(shadowPx, glowPx * 2.2f) else null
        // Brillo corto: se dibuja encima del borde, junto al destello
        val topGlowPaint = if (supportsBlur) blurPaint(shadowPx * 0.3f, glowPx) else null

        onDrawWithContent {
            if (totalLength <= 0f || segLength <= 0f) {
                drawContent()
                return@onDrawWithContent
            }

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

            fun drawBlurred(paint: android.graphics.Paint, alpha: Float) {
                paint.shader = android.graphics.LinearGradient(
                    tailPos.x, tailPos.y, headPos.x, headPos.y,
                    intArrayOf(
                        Color.Transparent.toArgb(),
                        midColor.copy(alpha = alpha).toArgb(),
                        headColor.copy(alpha = alpha).toArgb()
                    ),
                    floatArrayOf(0f, 0.6f, 1f),
                    android.graphics.Shader.TileMode.CLAMP
                )
                drawIntoCanvas { it.nativeCanvas.drawPath(segment.asAndroidPath(), paint) }
            }

            // 1. Sombra hacia afuera (detrás del botón)
            if (outerShadowPaint != null) {
                drawBlurred(outerShadowPaint, 0.8f)
            } else {
                // Respaldo para Android < 9: capas anchas y tenues simulan el brillo
                listOf(2.6f to 0.10f, 1.8f to 0.18f, 1.0f to 0.35f).forEach { (widthFactor, alpha) ->
                    drawPath(
                        path = segment,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                midColor.copy(alpha = alpha),
                                headColor.copy(alpha = alpha * 1.5f)
                            ),
                            start = tailPos,
                            end = headPos
                        ),
                        style = Stroke(width = glowPx * widthFactor, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            // 2. El botón
            drawContent()

            // 3. Brillo corto y destello encima del borde
            topGlowPaint?.let { drawBlurred(it, 0.9f) }
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
                style = Stroke(width = beamPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

