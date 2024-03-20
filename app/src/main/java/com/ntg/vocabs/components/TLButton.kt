package com.ntg.vocabs.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.ui.theme.fontMedium16


@Composable
fun TLButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick:()-> Unit
) {




    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .flickerAnimation()
            .fillMaxWidth()
            .clickable {
                       onClick.invoke()
            }
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 16.dp),

            text = text,
            style = fontMedium16(MaterialTheme.colorScheme.onPrimary),
        )


    }

}


fun Modifier.flickerAnimation(
    widthOfShadowBrush: Int = 600,
    angleOfAxisY: Float = 0f,
    durationMillis: Int = 2500,
): Modifier {
    return composed {

        val shimmerColors = getColours()

        val transition = rememberInfiniteTransition(label = "")

        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart,
            ),
            label = "flicker animation",
        )

        this.background(
            brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
                end = Offset(x = translateAnimation.value, y = angleOfAxisY),
            ),
        )
    }

}


private fun getColours(): List<Color> {
    val color = Color.White
    return listOf(
        color.copy(alpha = 0.1f),
        color.copy(alpha = 0.2f),
        color.copy(alpha = 0.4f),
        color.copy(alpha = 0.4f),
        color.copy(alpha = 0.2f),
        color.copy(alpha = 0.1f),
    )
}