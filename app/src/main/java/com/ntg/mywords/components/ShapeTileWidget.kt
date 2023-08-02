package com.ntg.mywords.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.*

@Composable
fun ShapeTileWidget(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    painter: Painter,
    aspectRatio: Float = 2.75f,
    imageTint: Color? = null,
    onClick:() -> Unit ={}

    ) {


    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Secondary100)
            .aspectRatio(aspectRatio)
            .clickable(
                onClick = onClick,
                indication = rememberRipple(
                    color = Secondary500
                ),
                interactionSource = remember { MutableInteractionSource() }
            )

    ) {
        Row(modifier = Modifier.align(Alignment.Center)
            .padding(horizontal = 8.dp)) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .align(Alignment.CenterVertically)
            ) {

                Image(
                    modifier = Modifier.padding(8.dp),
                    painter = painter,
                    contentDescription = "img"
                )
            }

            Box(modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center) {

                Column {
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, style = FontBold12(Secondary800))
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally),text = subTitle, style = FontRegular12(Secondary500))

                }


            }


        }
    }


}