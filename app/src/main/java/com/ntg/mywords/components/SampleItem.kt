package com.ntg.mywords.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.*

@Composable
fun SampleItem(
    modifier: Modifier = Modifier,
    id: Int? = null,
    title: String,
    painter: Painter? = null,
    isBookmarked: Boolean = false,
    enableRadioButton: Boolean = false,
    radioSelect: MutableState<Boolean> = remember { mutableStateOf(false) },
    onClick: ((String, Int?, Boolean?) -> Unit)? = null,

    ) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = {
                    onClick?.invoke(title, id, radioSelect.value)
                },
                indication = rememberRipple(
                    color = Secondary300
                ),
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Row {
            if (enableRadioButton) {
                RadioButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    selected = radioSelect.value,
                    onClick = {
                        onClick?.invoke(title, id, radioSelect.value)
                    })
            }

            if (listOf("die", "das", "der").contains(title)){

                Box(modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 8.dp)
                    .background(shape = RoundedCornerShape(32.dp),
                        color = when(title){

                            "die" ->{
                                Color.Red
                            }

                            "der" -> {
                                Color.Blue
                            }

                            "das" -> {
                                Color.Green
                            }

                            else -> Color.Green

                        }
                    ).size(8.dp))



            }

            Text(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(horizontal = 4.dp)
                    .weight(1f),
                text = title,
                style = fontMedium16(MaterialTheme.colorScheme.onSurfaceVariant)
            )

            if (isBookmarked) {
                Icon(
                    Icons.Rounded.Bookmark,
                    contentDescription = "bookmarked",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp)
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (painter != null) {
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp),
                    painter = painter,
                    contentDescription = "imageSampleItem"
                )
            }

        }
    }
}

@Preview
@Composable
private fun SampleItemPreview() {
    SampleItem(title = "test", isBookmarked = true)
}