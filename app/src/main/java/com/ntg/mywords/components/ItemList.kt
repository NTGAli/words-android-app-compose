package com.ntg.mywords.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.ui.theme.fontMedium16

@Composable
fun ItemList(
    modifier: Modifier = Modifier,
    id: Int,
    title: String,
    subTitle: String,
    isSelected: Boolean,
    onClick:(Int) -> Unit
){


    Column(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(width = 2.dp, shape = RoundedCornerShape(16.dp), color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        .clickable {
            onClick.invoke(id)
        }){

        Text(modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp), text = title, style = fontMedium16(
            MaterialTheme.colorScheme.primary)
        )

        Text(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp).padding(horizontal = 16.dp), text = subTitle, style = fontMedium14(
            MaterialTheme.colorScheme.onBackground)
        )

    }

}