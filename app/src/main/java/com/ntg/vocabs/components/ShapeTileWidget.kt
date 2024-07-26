package com.ntg.vocabs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
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
import com.ntg.vocabs.ui.theme.*
import com.ntg.vocabs.util.WindowInfo
import com.ntg.vocabs.util.rememberWindowInfo

@Composable
fun ShapeTileWidget(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    painter: Painter,
    aspectRatio: Float = 2.75f,
    imageTint: Color,
    imageBackground: Color = Color.White,
    onClick:() -> Unit ={}

    ) {

    val windowInfo = rememberWindowInfo()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
//            .border(width = 1.dp, color = imageTint, shape = RoundedCornerShape(8.dp))
//            .aspectRatio(aspectRatio)

            .clickable(
                onClick = onClick,
                indication = rememberRipple(
                    color = Secondary500
                ),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 8.dp)

    ) {
        Row(modifier = Modifier
            .align(Alignment.Center)
            .padding(horizontal = 8.dp)) {
            Icon(
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .fillMaxSize()
                    .background(imageBackground, shape = RoundedCornerShape(8.dp))
                    .padding(4.dp)
                ,
                painter = painter,
                contentDescription = "img",
                tint = imageTint ?: Color.Black
//                colorFilter= MaterialTheme.colorScheme.onPrimary,
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(modifier = Modifier, text = title, style = if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) fontBold12(MaterialTheme.colorScheme.onBackground) else fontBold14(MaterialTheme.colorScheme.onBackground))
                Text(modifier = Modifier,text = subTitle, style = if(windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) fontRegular12(MaterialTheme.colorScheme.outlineVariant) else fontRegular14(MaterialTheme.colorScheme.outlineVariant))
            }


        }
    }


}