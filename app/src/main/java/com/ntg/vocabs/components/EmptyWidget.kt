package com.ntg.vocabs.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.ui.theme.fontRegular14

@Composable
fun EmptyWidget(
    modifier: Modifier = Modifier,
    title: String
){

    Box(
        modifier = modifier.fillMaxWidth().border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
        ){
            Text(modifier = Modifier.padding(vertical = 16.dp),text = title, style = fontRegular14(
                MaterialTheme.colorScheme.secondary))
        }


}