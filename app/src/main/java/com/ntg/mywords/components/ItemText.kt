package com.ntg.mywords.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.fontBold12
import com.ntg.mywords.ui.theme.Secondary500

@Composable
fun ItemText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (String) -> Unit
) {


    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .wrapContentSize()
            .clickable(
                onClick = {
                    onClick.invoke(text)
                },
                indication = rememberRipple(
                    color = Secondary500,
                    radius = 8.dp
                ),
                interactionSource = remember { MutableInteractionSource() }
            )

    ) {

        Text(modifier = Modifier
            .align(Alignment.Center)
            .padding(8.dp),text = text, style = fontBold12(MaterialTheme.colorScheme.onSurfaceVariant))

    }

}

@Preview
@Composable
private fun ItemTextPreview(){
    ItemText(text = "test", onClick = {})
}