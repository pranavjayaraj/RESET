package com.reset.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

/**
 * Tiny Canvas icons that mirror the inline SVGs in the design, avoiding a
 * material-icons dependency. All paths are authored on a 24x24 grid and scaled.
 */

private const val GRID = 24f

@Composable
fun GearIcon(modifier: Modifier = Modifier, tint: Color = AppColors.textPrimary) {
    Canvas(modifier) {
        val s = size.minDimension
        val unit = s / GRID
        val stroke = 1.8f * unit
        drawCircle(tint, radius = 3.2f * unit, center = center, style = Stroke(stroke))
        val rInner = 5.5f * unit
        val rOuter = 9.5f * unit
        repeat(8) { i ->
            val a = Math.toRadians((i * 45).toDouble())
            val dx = cos(a).toFloat()
            val dy = sin(a).toFloat()
            drawLine(
                color = tint,
                start = Offset(center.x + dx * rInner, center.y + dy * rInner),
                end = Offset(center.x + dx * rOuter, center.y + dy * rOuter),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun BackIcon(modifier: Modifier = Modifier, tint: Color = AppColors.textPrimary) {
    Canvas(modifier) {
        val unit = size.minDimension / GRID
        fun p(x: Float, y: Float) = Offset(x * unit, y * unit)
        val path = Path().apply {
            moveTo(p(15f, 5f).x, p(15f, 5f).y)
            lineTo(p(8f, 12f).x, p(8f, 12f).y)
            lineTo(p(15f, 19f).x, p(15f, 19f).y)
        }
        drawPath(path, tint, style = Stroke(2f * unit, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun HomeIcon(modifier: Modifier = Modifier, tint: Color = AppColors.textPrimary) {
    Canvas(modifier) {
        val unit = size.minDimension / GRID
        fun p(x: Float, y: Float) = Offset(x * unit, y * unit)
        val stroke = Stroke(1.8f * unit, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val roof = Path().apply {
            moveTo(p(4f, 11.5f).x, p(4f, 11.5f).y)
            lineTo(p(12f, 4.5f).x, p(12f, 4.5f).y)
            lineTo(p(20f, 11.5f).x, p(20f, 11.5f).y)
        }
        drawPath(roof, tint, style = stroke)
        val walls = Path().apply {
            moveTo(p(6.5f, 10.5f).x, p(6.5f, 10.5f).y)
            lineTo(p(6.5f, 19.5f).x, p(6.5f, 19.5f).y)
            lineTo(p(17.5f, 19.5f).x, p(17.5f, 19.5f).y)
            lineTo(p(17.5f, 10.5f).x, p(17.5f, 10.5f).y)
        }
        drawPath(walls, tint, style = stroke)
    }
}

/** Concentric ripple rings — the sit-history / sessions marker. */
@Composable
fun RippleIcon(modifier: Modifier = Modifier, tint: Color = AppColors.textPrimary) {
    Canvas(modifier) {
        val unit = size.minDimension / GRID
        val stroke = Stroke(1.8f * unit, cap = StrokeCap.Round)
        drawCircle(tint, radius = 2.6f * unit, center = center, style = stroke)
        drawCircle(tint.copy(alpha = tint.alpha * 0.7f), radius = 6f * unit, center = center, style = stroke)
        drawCircle(tint.copy(alpha = tint.alpha * 0.4f), radius = 9.4f * unit, center = center, style = stroke)
    }
}

@Composable
fun ProfileIcon(modifier: Modifier = Modifier, tint: Color = AppColors.textPrimary) {
    Canvas(modifier) {
        val unit = size.minDimension / GRID
        fun p(x: Float, y: Float) = Offset(x * unit, y * unit)
        val stroke = Stroke(1.8f * unit, cap = StrokeCap.Round)
        drawCircle(tint, radius = 3.4f * unit, center = p(12f, 8f), style = stroke)
        val shoulders = Path().apply {
            moveTo(p(5f, 19.5f).x, p(5f, 19.5f).y)
            quadraticTo(p(12f, 13f).x, p(12f, 13f).y, p(19f, 19.5f).x, p(19f, 19.5f).y)
        }
        drawPath(shoulders, tint, style = stroke)
    }
}

@Composable
fun ClosedEyeIcon(modifier: Modifier = Modifier, tint: Color = AppColors.textPrimary) {
    Canvas(modifier) {
        val unit = size.minDimension / GRID
        fun x(v: Float) = v * unit
        fun y(v: Float) = v * unit
        val stroke = Stroke(2f * unit, cap = StrokeCap.Round)
        // downward eye curve
        val lid = Path().apply {
            moveTo(x(4f), y(10f))
            quadraticTo(x(12f), y(16f), x(20f), y(10f))
        }
        drawPath(lid, tint, style = stroke)
        // three short lashes
        drawLine(tint, Offset(x(7f), y(13.4f)), Offset(x(6f), y(15.4f)), 2f * unit, StrokeCap.Round)
        drawLine(tint, Offset(x(12f), y(14.8f)), Offset(x(12f), y(17f)), 2f * unit, StrokeCap.Round)
        drawLine(tint, Offset(x(17f), y(13.4f)), Offset(x(18f), y(15.4f)), 2f * unit, StrokeCap.Round)
    }
}
