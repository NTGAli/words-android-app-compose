package com.ntg.mywords.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.ui.theme.Secondary500

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    type:ButtonType = ButtonType.Primary,
    size: ButtonSize = ButtonSize.MD,
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
            background = Secondary500
            textColor = Color.White
        }
        ButtonType.Success -> {}
        ButtonType.Secondary -> {}
        ButtonType.Warning -> {}
        ButtonType.Danger -> {}
        ButtonType.Info -> {}

    }



    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(roundCorner))
        .background(background)
        .clickable {
                   onClick()
        },
        contentAlignment = Alignment.Center)
    {

        Row(modifier = Modifier.align(Alignment.Center)) {

            Text(modifier = Modifier.padding(start = left, top = top, end = right, bottom = bottom), text = text)

        }

    }

}