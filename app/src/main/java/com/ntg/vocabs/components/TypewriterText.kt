package com.ntg.vocabs.components

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ntg.vocabs.ui.theme.fontBold24
import com.ntg.vocabs.util.timber
import kotlinx.coroutines.delay
import kotlin.streams.toList

@Composable
fun TypewriterText(
    modifier: Modifier = Modifier,
    texts:List<String>,
    cursor: String="",
    speedType: Long = 40L,
    delayTime: Long = 2000,
    singleText: Boolean = false,
    enableVibrate: Boolean = true,
    style: androidx.compose.ui.text.TextStyle = fontBold24(),
    onFinished:(Boolean) -> Unit = {}
) {

    val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            LocalContext.current.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        LocalContext.current.getSystemService(VIBRATOR_SERVICE) as Vibrator
    }


    var textIndex by remember {
        mutableStateOf(0)
    }
    var textToDisplay by remember {
        mutableStateOf("")
    }
    val textCharsList =
            texts.map {
                it.splitToCodePoints()
            }

    LaunchedEffect(
        key1 = texts,
    ) {
        while (textIndex < textCharsList.size) {
            textCharsList[textIndex].forEachIndexed { charIndex, _ ->
                textToDisplay = textCharsList[textIndex]
                    .take(
                        n = charIndex + 1,
                    ).joinToString(
                        separator = "",
                    )
                delay(speedType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && enableVibrate) {
//                    vib.vibrate(VibrationEffect.createOneShot(3L,20))
                }

            }
            textIndex = (textIndex + 1) % texts.size
            delay(delayTime)

            if (singleText) break

            textToDisplay.forEach {
                textToDisplay = textToDisplay.dropLast(1)
                delay(5)
            }
        }
    }

    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        Text(
            text = textToDisplay,
            style = fontBold24(MaterialTheme.colorScheme.onSurface)
        )
        Text(
            text = cursor,
            style = style
        )

        onFinished.invoke(
            textToDisplay.length == texts.first().length
        )

    }
}

fun String.splitToCodePoints(): List<String> {
    return codePoints()
        .toList()
        .map {
            String(Character.toChars(it))
        }
}