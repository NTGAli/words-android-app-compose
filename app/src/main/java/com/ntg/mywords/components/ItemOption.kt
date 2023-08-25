package com.ntg.mywords.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.ui.theme.fontRegular14

@Composable
fun ItemOption(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    text: String,
    divider: Boolean = true,
    onClick:()-> Unit
) {

    Column {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .clickable {
                    onClick.invoke()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (painter != null){
                Spacer(modifier = Modifier.padding(start = 32.dp))
            }

            if (painter != null){
                Icon(
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .padding(end = 8.dp)
                        .padding(vertical = 12.dp), painter = painter, contentDescription = "ic"
                )
            }

            Text(modifier= Modifier
                .padding(end = 32.dp)
                .padding(vertical = 12.dp),text = text, style = fontRegular14(MaterialTheme.colorScheme.onBackground))
        }

        if (divider){
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding( horizontal =  if (painter != null)32.dp else 0.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }

    }
}