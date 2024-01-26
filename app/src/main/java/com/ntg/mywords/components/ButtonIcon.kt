package com.ntg.mywords.components

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ntg.mywords.R
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.ui.theme.Primary100
import com.ntg.mywords.ui.theme.Primary900
import com.ntg.mywords.ui.theme.fontRegular12
import kotlinx.coroutines.delay
import java.io.File
import java.time.format.TextStyle
import java.util.Timer
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun ButtonIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    bitmap: Bitmap? = null,
    subText: String? = null,
    removeBtn:Boolean = false,
    enable: Boolean = true,
    removeOnClick:() -> Unit = {},
    onCLick:() -> Unit ={},
){


    Column(modifier = modifier) {

        Box(modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            .fillMaxWidth()
            .clickable(enabled = enable) {
                onCLick.invoke()
            },
            contentAlignment = Alignment.Center){

            if (bitmap == null){
                Icon(modifier = Modifier
                    .padding(vertical = 16.dp)
                    .alpha(
                        if (enable) 1f else 0.5f
                    ),painter = painterResource(id = icon), contentDescription = null)
            }else{
                Box(modifier = Modifier.aspectRatio(4f), contentAlignment = Alignment.Center){
                    Image(modifier = Modifier.fillMaxSize(),bitmap = bitmap.asImageBitmap(), contentDescription = "Selected image", contentScale = ContentScale.Crop)
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center){
                        Icon(painter = painterResource(id = R.drawable.remove_rectangle), contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }

            }



        }

        if (subText != null){
            Text(text = subText, style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))
        }

        if (removeBtn){
            CustomButton(text = stringResource(id = R.string.remove_audio), type = ButtonType.Danger, style = ButtonStyle.TextOnly, size = ButtonSize.SM){
                removeOnClick.invoke()
            }
        }
    }




}
