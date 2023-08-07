package com.ntg.mywords.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.ui.theme.*

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    type:ButtonType = ButtonType.Primary,
    size: ButtonSize = ButtonSize.MD,
    style: ButtonStyle = ButtonStyle.Contained,
    roundCorner: Dp = 8.dp,
    paddingLeft: Dp = 0.dp,
    paddingRight: Dp = 0.dp,
    paddingTop: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,

    onClick:() -> Unit ={}

) {


    var top = paddingTop
    var bottom = paddingBottom
    var left = paddingLeft
    var right = paddingRight

    var textColor = Color.White
    var background = Secondary500
    var borderColor = Secondary500

    when (size){

        ButtonSize.XL -> {
            left = 24.dp
            right = 24.dp
            top = 16.dp
            bottom = 16.dp
        }
        ButtonSize.LG -> {
            left = 20.dp
            right = 20.dp
            top = 14.dp
            bottom = 14.dp
        }
        ButtonSize.MD -> {
            left = 16.dp
            right = 16.dp
            top = 10.dp
            bottom = 10.dp
        }
        ButtonSize.SM -> {
            left = 12.dp
            right = 12.dp
            top = 7.dp
            bottom = 7.dp
        }
        ButtonSize.XS -> {
            left = 8.dp
            right = 8.dp
            top = 3.dp
            bottom = 3.dp
        }

    }


    when (type){

        ButtonType.Primary -> {

            when(style){
                ButtonStyle.Contained -> {
                    background = MaterialTheme.colorScheme.primaryContainer
                    borderColor = MaterialTheme.colorScheme.primaryContainer
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = MaterialTheme.colorScheme.primaryContainer
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                }
            }

        }
        ButtonType.Success -> {
            when(style){
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
            when(style){
                ButtonStyle.Contained -> {
                    background = Secondary500
                    borderColor = Secondary500
                    textColor = Color.White
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
            when(style){
                ButtonStyle.Contained -> {
                    background = Warning500
                    borderColor = Warning500
                    textColor = Color.White
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = Warning500
                    textColor = Warning500
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = Warning500
                }
            }
        }
        ButtonType.Danger -> {
            when(style){
                ButtonStyle.Contained -> {
                    background = Danger500
                    borderColor = Danger500
                    textColor = Color.White
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = Danger500
                    textColor = Danger500
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = Danger500
                }
            }
        }
        ButtonType.Info -> {
            when(style){
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

    }



    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(roundCorner))
        .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(roundCorner))
        .background(background)
        .clickable {
                   onClick()
        },
        contentAlignment = Alignment.Center)
    {

        Row(modifier = Modifier.align(Alignment.Center)) {

            Text(modifier = Modifier.padding(start = left, top = top, end = right, bottom = bottom), text = text, color = textColor)

        }

    }

}