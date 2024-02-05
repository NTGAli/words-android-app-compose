package com.ntg.vocabs.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium14

@Composable
fun DefinitionItem(
    modifier: Modifier,
    definition: String,
    example: List<String> = listOf(),
    isSelected: Boolean = false,
    onClick: () -> Unit
) {

    Column(modifier = modifier
        .border(
            shape = RoundedCornerShape(16.dp),
            width = 2.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        )
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .clickable {
            onClick.invoke()
        }) {

        Text(
            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp, bottom = 12.dp),
            text = definition,
            style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        example.forEach {
            if (it.isNotEmpty()){
                Text(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 4.dp),text = it, style = fontMedium12(MaterialTheme.colorScheme.outline))
            }
        }

        if (example.any { it.isNotEmpty() }){
            Spacer(modifier = Modifier.padding(12.dp))
        }

    }

}