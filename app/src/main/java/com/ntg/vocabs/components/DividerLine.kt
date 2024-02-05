package com.ntg.vocabs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.ui.theme.fontMedium14

@Composable
fun DividerLine(
    modifier: Modifier = Modifier,
    title: String
){

    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        Divider(color = MaterialTheme.colorScheme.outlineVariant)
        Text(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(horizontal = 8.dp), text = title, style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant))


    }


}