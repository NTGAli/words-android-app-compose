package com.ntg.vocabs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.ui.theme.*

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    iconStart: Painter? = null,
    type: ButtonType = ButtonType.Primary,
    size: ButtonSize = ButtonSize.MD,
    style: ButtonStyle = ButtonStyle.Contained,
    enable: Boolean = true,
    loading: Boolean = false,
    roundCorner: Dp = 8.dp,
    paddingLeft: Dp = 0.dp,
    paddingRight: Dp = 0.dp,
    paddingTop: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,

    onClick: () -> Unit = {}

) {


    var top = paddingTop
    var bottom = paddingBottom
    var left = paddingLeft
    var right = paddingRight

    var textColor = Color.White
    var background = Secondary500
    var borderColor = Secondary500
    var loadingColor = MaterialTheme.colorScheme.onPrimary
    var textStyle = fontMedium14(textColor)

    when (type) {

        ButtonType.Primary -> {

            when (style) {
                ButtonStyle.Contained -> {
                    background = MaterialTheme.colorScheme.primary
                    borderColor = MaterialTheme.colorScheme.primary
                    textColor = MaterialTheme.colorScheme.onPrimary
                    loadingColor = MaterialTheme.colorScheme.onPrimary
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = MaterialTheme.colorScheme.primary
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = MaterialTheme.colorScheme.primary
                }
            }

        }
        ButtonType.Success -> {
            when (style) {
                ButtonStyle.Contained -> {
                    background = Success500
                    borderColor = Success500
                    textColor = Color.White
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = Success500
                    textColor = Success500
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = Success500
                }
            }
        }
        ButtonType.Secondary -> {
            when (style) {
                ButtonStyle.Contained -> {
                    background = MaterialTheme.colorScheme.surfaceVariant
                    borderColor = MaterialTheme.colorScheme.surfaceVariant
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = Secondary500
                    textColor = Secondary500
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = Secondary500
                }
            }
        }
        ButtonType.Warning -> {
            when (style) {
                ButtonStyle.Contained -> {
                    background = Warning500
                    borderColor = Warning500
                    textColor = Color.White
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = Warning800
                    textColor = Warning800
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = Warning800
                }
            }
        }
        ButtonType.Danger -> {
            when (style) {
                ButtonStyle.Contained -> {
                    background = MaterialTheme.colorScheme.error
                    borderColor = MaterialTheme.colorScheme.error
                    textColor = MaterialTheme.colorScheme.onError
                    loadingColor = MaterialTheme.colorScheme.onError
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = MaterialTheme.colorScheme.error
                    textColor = MaterialTheme.colorScheme.onErrorContainer
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = MaterialTheme.colorScheme.error
                }
            }
        }
        ButtonType.Info -> {
            when (style) {
                ButtonStyle.Contained -> {
                    background = Info500
                    borderColor = Info500
                    textColor = Color.White
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = Info500
                    textColor = Info500
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = Info500
                }
            }
        }

        ButtonType.Variance -> {
            when (style) {
                ButtonStyle.Contained -> {
                    background = MaterialTheme.colorScheme.surfaceVariant
                    borderColor = MaterialTheme.colorScheme.surfaceVariant
                    textColor = MaterialTheme.colorScheme.onSurface
                    loadingColor = MaterialTheme.colorScheme.onSurface
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = MaterialTheme.colorScheme.surfaceVariant
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                }
            }
        }
    }


    when (size) {

        ButtonSize.XL -> {
            left = 24.dp
            right = 24.dp
            top = 16.dp
            bottom = 16.dp
            textStyle = fontMedium16(textColor)
        }
        ButtonSize.LG -> {
            left = 20.dp
            right = 20.dp
            top = 14.dp
            bottom = 14.dp
            textStyle = fontMedium14(textColor)
        }
        ButtonSize.MD -> {
            left = 16.dp
            right = 16.dp
            top = 10.dp
            bottom = 10.dp
            textStyle = fontMedium14(textColor)
        }
        ButtonSize.SM -> {
            left = 12.dp
            right = 12.dp
            top = 7.dp
            bottom = 7.dp
            textStyle = fontMedium12(textColor)
        }
        ButtonSize.XS -> {
            left = 8.dp
            right = 8.dp
            top = 3.dp
            bottom = 3.dp
            textStyle = fontMedium12(textColor)
        }

    }



    Box(
        modifier = modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(roundCorner))
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(roundCorner))
            .background(background)
            .clickable(enabled = enable && !loading) {
                onClick()
            },
        contentAlignment = Alignment.Center
    )
    {
        Row(modifier = Modifier
            .align(Alignment.Center)
            .padding(start = left, top = top, end = right, bottom = bottom)) {
            if (!loading){
                
                if (iconStart != null){
                    Icon(painter = iconStart, contentDescription = "icon start",tint = if (type == ButtonType.Variance) Color.Unspecified else textColor)
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                }
                
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = text,
                    color = textColor,
                    style = textStyle
                )
            }else{
                CircularProgressIndicator(modifier = Modifier
                    .progressSemantics()
                    .size(24.dp)
                    , color = loadingColor, strokeWidth = 3.dp)
            }
        }
    }

}