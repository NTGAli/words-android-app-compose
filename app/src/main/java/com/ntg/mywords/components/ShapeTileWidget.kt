package com.ntg.mywords.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
            .background(MaterialTheme.colorScheme.surfaceVariant)
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
                    .padding(vertical = 10.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center

            ) {

                Icon(
                    modifier = Modifier.padding(8.dp)
                        .fillMaxSize(),
                    painter = painter,
                    contentDescription = "img",
                    tint = imageTint ?: Color.Black
//                colorFilter= MaterialTheme.colorScheme.onPrimary,
                )
            }

            Box(modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center) {

                Column {
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, style = FontBold12(MaterialTheme.colorScheme.onPrimaryContainer))
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally),text = subTitle, style = fontRegular12(MaterialTheme.colorScheme.onPrimaryContainer))

                }


            }


        }
    }


}