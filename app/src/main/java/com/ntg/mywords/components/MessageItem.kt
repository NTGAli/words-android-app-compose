package com.ntg.mywords.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.ui.theme.fontRegular12

@Composable
fun MessageItem(
    modifier: Modifier =  Modifier,
    title: String,
    description: String,
    action: String,
    actionData: String,
    isSeen: Boolean = false,
    actionClick:(String) -> Unit
){

    Column(modifier = modifier.padding(vertical = 8.dp, horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant))
            if (!isSeen){
                Box(modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary).size(4.dp).padding(start = 8.dp))
            }
        }
        Text(modifier = Modifier.padding(8.dp), text = description, style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))
        CustomButton(text = action, size = ButtonSize.XS, type = ButtonType.Primary, style = ButtonStyle.TextOnly){
            actionClick.invoke(actionData)
        }
        Divider(modifier = Modifier.padding(top = 8.dp),color = MaterialTheme.colorScheme.surfaceVariant)
    }
}