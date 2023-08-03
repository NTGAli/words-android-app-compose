package com.ntg.mywords.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.*

@Composable
fun SampleItem(
    modifier: Modifier = Modifier,
    id: Int? = null,
    title: String,
    painter: Painter? = null,
    onClick:((String, Int?)  -> Unit)? = null,

) {

    Box(modifier = modifier
        .fillMaxWidth()
        .clickable(
            onClick = { onClick?.invoke(title, id) },
            indication = rememberRipple(
                color = Secondary300
            ),
            interactionSource = remember { MutableInteractionSource() }
        )

    ){
        Row {
            Text(modifier = Modifier.padding(vertical = 16.dp).weight(1f),text = title, style = fontMedium16(Secondary700))
            if (painter != null){
                Image(modifier= Modifier.align(Alignment.CenterVertically),painter = painter, contentDescription = "imageSampleItem")
            }

        }
    }


}