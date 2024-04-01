@file:OptIn(ExperimentalAnimationApi::class)

package com.ntg.vocabs.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.ntg.vocabs.ui.theme.fontMedium24
import org.intellij.lang.annotations.Language


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TextChange(
    firstText: MutableState<String> = remember { mutableStateOf("") },
    secondText: String = ""
) {
//    var text by remember {
//        mutableStateOf(firstText)
//    }

    val time = 1000

    val blur = remember { Animatable(0f) }

    LaunchedEffect(firstText.value) {
        blur.animateTo(30f, tween(time / 2, easing = LinearEasing))
        blur.animateTo(0f, tween(time / 2, easing = LinearEasing))
    }


    MetaContainer(
        modifier = Modifier
            .animateContentSize()
            .clipToBounds(),
        cutoff = .2f
    ) {
        MetaEntity(
            blur = blur.value,
            metaContent = {
                AnimatedContent(
                    targetState = firstText.value,
                    transitionSpec = {
                        fadeIn(tween(time, easing = LinearEasing)) + expandVertically(
                            tween(
                                time,
                                easing = LinearEasing
                            ), expandFrom = Alignment.CenterVertically
                        ) with fadeOut(
                            tween(
                                time,
                                easing = LinearEasing
                            )
                        ) + shrinkVertically(
                            tween(
                                time,
                                easing = LinearEasing
                            ), shrinkTowards = Alignment.CenterVertically
                        )
                    }, label = ""
                ) { text ->
                    Text(
                        text,
                        style = fontMedium24(MaterialTheme.colorScheme.onSurface)
                    )
                }
            }) {}
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MetaEntity(
    modifier: Modifier = Modifier,
    blur: Float = 30f,
    metaContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {

    Box(
        modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .customBlur(blur),
            content = metaContent,
        )
        content()
    }

}


@Language("AGSL")
const val ShaderSource = """
    uniform shader composable;
    
    uniform float cutoff;
    uniform float3 rgb;
    
    half4 main(float2 fragCoord) {
        half4 color = composable.eval(fragCoord);
        color.a = step(cutoff, color.a);
        if (color == half4(0.0, 0.0, 0.0, 1.0)) {
            color.rgb = half3(rgb[0], rgb[1], rgb[2]);
        }
        return color;
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MetaContainer(
    modifier: Modifier = Modifier,
    cutoff: Float = .5f,
    color: Color = Color.Black,
    content: @Composable BoxScope.() -> Unit,
) {
    val metaShader = remember {
        RuntimeShader(ShaderSource)
    }
    Box(
        modifier
            .graphicsLayer {
                metaShader.setFloatUniform("cutoff", cutoff)
                metaShader.setFloatUniform("rgb", color.red, color.green, color.blue)
                renderEffect = RenderEffect
                    .createRuntimeShaderEffect(
                        metaShader, "composable"
                    )
                    .asComposeRenderEffect()
            },
        content = content,
    )
}

@RequiresApi(Build.VERSION_CODES.S)
fun Modifier.customBlur(blur: Float) = this.then(
    graphicsLayer {
        if (blur > 0f)
            renderEffect = RenderEffect
                .createBlurEffect(
                    blur,
                    blur,
                    Shader.TileMode.DECAL,
                )
                .asComposeRenderEffect()
    }
)