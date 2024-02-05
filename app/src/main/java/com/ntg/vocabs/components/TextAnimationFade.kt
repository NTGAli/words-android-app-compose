package com.ntg.vocabs.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.ntg.vocabs.util.timber
import kotlinx.coroutines.delay

@Composable
fun TextAnimationFade(
    modifier: Modifier = Modifier,
    x: Dp,
    y: Dp,
//    visible : MutableState<Boolean> = remember { mutableStateOf(true) }
) {


    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val scale = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(5000), RepeatMode.Reverse),
        label = "scale"
    )

    val visible = remember { mutableStateOf(true) }


    LaunchedEffect(visible) {
        delay(3000) // to avoid repeated delays

        timber("tttttttttttttttttttttttttttttttttttttttt")
        visible.value = false
    }

    timber("akljdlkwajdklawjlkdjwlkajd $visible $x --- $y -${scale.value}")

    AnimatedVisibility(
        modifier = Modifier
            .offset(x = x, y = y)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                transformOrigin = TransformOrigin.Center
            },
        visible = visible.value,
        enter = fadeIn(
            // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
            initialAlpha = 0.5f
        ),
        exit = fadeOut(
            // Overwrites the default animation with tween
            animationSpec = tween(durationMillis = 3000)
        )
    ) {
        Text(
            modifier = Modifier.wrapContentSize(), text = "aaaa"
        )
    }

}