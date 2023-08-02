package com.ntg.mywords.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.FontBold12
import com.ntg.mywords.ui.theme.Secondary100
import com.ntg.mywords.ui.theme.Secondary500
import com.ntg.mywords.ui.theme.Secondary800
import org.w3c.dom.Text

@Composable
fun ItemText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (String) -> Unit
) {


    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Secondary100)
            .wrapContentSize()
            .clickable(
                onClick = {
                    onClick.invoke(text)
                },
                indication = rememberRipple(
                    color = Secondary500
                ),
                interactionSource = remember { MutableInteractionSource() }
            )

    ) {

        Text(modifier = Modifier.align(Alignment.Center).padding(8.dp),text = text, style = FontBold12(Secondary800))

    }

}