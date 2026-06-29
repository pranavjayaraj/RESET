package com.reset.feature.home.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.reset.feature.home.ui.theme.AfkColors
import com.reset.feature.home.ui.theme.AfkDimens
import com.reset.feature.home.ui.theme.AfkShapes
import com.reset.feature.home.ui.theme.AfkType
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/** Base orbit angles (degrees) for the four reset words, matching the design. */
private val ORBIT_ANGLES = listOf(225f, 315f, 135f, 45f)

private const val ORBIT_PERIOD_MS = 42_000
private const val BREATHE_PERIOD_MS = 9_000

/**
 * The breathing "Reset" bubble: a pulsing aura + rings, four words orbiting it,
 * and the central tappable bubble. When [leaving] is true the words spiral
 * outward and fade as the sit begins.
 */
@Composable
fun BreathingBubble(
    topLabel: String,
    title: String,
    subLabel: String,
    contentDescription: String,
    words: List<String>,
    leaving: Boolean,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "bubble")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(ORBIT_PERIOD_MS, easing = LinearEasing)),
        label = "orbit",
    )
    val breathe by transition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(BREATHE_PERIOD_MS), repeatMode = RepeatMode.Reverse),
        label = "breathe",
    )

    val density = LocalDensity.current
    val orbitRadiusPx = with(density) { AfkDimens.orbitRadius.toPx() }
    val flungRadiusPx = with(density) { 600.dp.toPx() }

    Box(modifier = modifier.size(AfkDimens.ringB), contentAlignment = Alignment.Center) {
        // pulsing aura
        Box(
            Modifier
                .size(AfkDimens.aura)
                .scale(breathe)
                .alpha((breathe - 0.55f).coerceIn(0.4f, 1f))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.26f), Color.Transparent),
                    ),
                    CircleShape,
                ),
        )
        // concentric rings
        Box(Modifier.size(AfkDimens.ringB).clip(CircleShape).border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape))
        Box(Modifier.size(AfkDimens.ringA).clip(CircleShape).border(1.dp, AfkColors.glassBorder, CircleShape))

        // orbiting words
        val wordAlpha = if (leaving) 0f else 1f
        words.forEachIndexed { i, word ->
            val baseAngle = ORBIT_ANGLES.getOrElse(i) { 0f }
            val angleRad = Math.toRadians((baseAngle + rotation).toDouble())
            val radius = if (leaving) flungRadiusPx else orbitRadiusPx
            Box(
                Modifier
                    .offset {
                        IntOffset(
                            x = (cos(angleRad) * radius).roundToInt(),
                            y = (sin(angleRad) * radius).roundToInt(),
                        )
                    }
                    .alpha(wordAlpha)
                    .glass(AfkShapes.pill)
                    .padding(horizontal = 15.dp, vertical = 8.dp),
            ) {
                Text(
                    text = word,
                    style = AfkType.bubbleSub.copy(fontSize = AfkType.bubbleTop.fontSize),
                    color = AfkColors.textStrong,
                )
            }
        }

        // central bubble
        Box(
            Modifier
                .size(AfkDimens.bubbleSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E3C34).copy(alpha = 0.32f),
                            Color(0xFF142D28).copy(alpha = 0.42f),
                        ),
                    ),
                    CircleShape,
                )
                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .clickable(role = Role.Button, onClick = onReset)
                .semantics { this.contentDescription = contentDescription },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(topLabel, style = AfkType.bubbleTop, color = AfkColors.textSecondary)
                Text(title, style = AfkType.bubbleMain, color = AfkColors.textPrimary, textAlign = TextAlign.Center)
                Text(subLabel, style = AfkType.bubbleSub, color = AfkColors.textTertiary)
            }
        }
    }
}
