package com.ntg.vocabs.components

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.FloatRange
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.hypot

fun Modifier.circularReveal(@FloatRange(from = 0.0, to = 1.0) progress: Float, offset: Offset? = null) = clip(CircularRevealShape(progress, offset))

private class CircularRevealShape(
    @FloatRange(from = 0.0, to = 1.0) private val progress: Float,
    private val offset: Offset? = null
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(Path().apply {
            addCircle(
                offset?.x ?: (size.width / 2f),
                offset?.y ?: (size.height / 2f),
                size.width.coerceAtLeast(size.height) * 2 * progress,
                Path.Direction.CW
            )
        }.asComposePath())
    }
}