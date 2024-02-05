package com.ntg.vocabs.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.ui.theme.fontRegular12

@Composable
fun CheckboxText(
    modifier: Modifier = Modifier,
    checked: MutableState<Boolean> = remember { mutableStateOf(false) },
    enabled: Boolean = true,
    text: String
){
    Row(modifier = modifier) {
        Card(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            ),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.5.dp, color = if (checked.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .background(if (checked.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                    .clickable(enabled = enabled) {
                        checked.value = !checked.value
                    },
                contentAlignment = Center
            ) {
                if(checked.value)
                    Icon(Icons.Default.Check, contentDescription = "", tint = Color.White)
            }
        }

        Text(modifier = Modifier.padding(start = 8.dp), text = text, style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))
    }
}