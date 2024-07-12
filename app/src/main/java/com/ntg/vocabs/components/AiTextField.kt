package com.ntg.vocabs.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.R
import com.ntg.vocabs.ui.theme.Danger500
import com.ntg.vocabs.ui.theme.Primary500
import com.ntg.vocabs.ui.theme.Success500
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium16

@Composable
fun AiTextField(
    modifier: Modifier = Modifier,
    text: MutableState<String>,
    placeHolder: String,
    state: AiTextFieldStateState = rememberAiTextFieldState()
) {


    val infiniteTransition = rememberInfiniteTransition(label = "")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val Gradient =
        if (state.state == AiTextFieldStateStates.Generating){
            listOf(
                Success500,
                Primary500,
                Danger500
            )
        }else{
            listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surfaceVariant,
            )
        }

    val brush = remember(offset) {
        object : ShaderBrush() {
            override fun createShader(size: androidx.compose.ui.geometry.Size): Shader {
                val widthOffset = size.width * offset
                val heightOffset = size.height * offset
                return LinearGradientShader(
                    colors = Gradient,
                    from = Offset(widthOffset, heightOffset),
                    to = Offset(widthOffset + size.width, heightOffset + size.height),
                    tileMode = TileMode.Mirror
                )
            }
        }
    }





    Column(
        modifier = modifier.border(
            width = 1.dp,
            shape = RoundedCornerShape(16.dp),
            brush = brush
        )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                modifier = Modifier.padding(16.dp),
                painter = painterResource(id = R.drawable.stars),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                value = text.value,
                onValueChange = {
                    if (it.length <= 250) {
                        text.value = it
                    }
                },
                minLines = 3,
                singleLine = false,
                textStyle = fontMedium16(MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )

            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.microphone_02),
                    contentDescription = null
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(vertical = 16.dp)
                    .weight(1f),
                text = if (state.state == AiTextFieldStateStates.Generating) stringResource(id = R.string.ai_generating) else "",
                style = fontMedium12(MaterialTheme.colorScheme.primary)
            )

            Text(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .padding(vertical = 16.dp),
                text = stringResource(id = R.string.prompt_left_format, (250 - text.value.length)),
                style = fontMedium12(MaterialTheme.colorScheme.onBackground)
            )

        }

    }


}


enum class AiTextFieldStateStates {
    Default,
    Generating,
    Finished
}

class AiTextFieldStateState(initial: AiTextFieldStateStates) {
    var state: AiTextFieldStateStates by mutableStateOf(initial)
}

val AiTextFieldStateSaver: Saver<AiTextFieldStateState, *> = Saver(
    save = { it.state.name },
    restore = { AiTextFieldStateState(AiTextFieldStateStates.valueOf(it)) }
)

@Composable
fun rememberAiTextFieldState(initial: AiTextFieldStateStates = AiTextFieldStateStates.Default): AiTextFieldStateState {
    return rememberSaveable(saver = AiTextFieldStateSaver) {
        AiTextFieldStateState(initial)
    }
}