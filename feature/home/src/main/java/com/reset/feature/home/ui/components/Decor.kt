package com.reset.feature.home.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.reset.feature.home.ui.theme.AfkColors

/**
 * The frosted "glass" recipe from the design. Real backdrop blur is API 31+, so
 * over the gradient this is approximated with translucent fill + a hairline border.
 */
fun Modifier.glass(shape: Shape): Modifier = this
    .background(AfkColors.glassFill, shape)
    .border(1.dp, AfkColors.glassBorder, shape)

/** Faint concentric arcs echoing the reference page, drawn behind the content. */
@androidx.compose.runtime.Composable
fun ConcentricArcs(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val step = 87.dp.toPx()
        val originX = size.width * 0.5f
        val originY = size.height * 0.30f
        val maxR = kotlin.math.hypot(size.width, size.height)
        var r = step
        while (r < maxR) {
            drawCircle(
                color = AfkColors.arcDecoration,
                radius = r,
                center = Offset(originX, originY),
                style = Stroke(width = 1.dp.toPx()),
            )
            r += step
        }
    }
}
