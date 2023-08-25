package com.ntg.mywords.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import com.ntg.mywords.ui.theme.fontRegular14

@Composable
fun ItemSelectable(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick:(String) -> Unit = {}
){

    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(width = 2.dp, shape = RoundedCornerShape(16.dp), color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        .clickable {
            onClick.invoke(text)
        }){
        
        Text(modifier = Modifier.padding(vertical = 16.dp).padding(start = 24.dp), text = text, style = fontMedium14(MaterialTheme.colorScheme.onBackground))
        
    }

}