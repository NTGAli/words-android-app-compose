package com.ntg.vocabs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontRegular12

@Composable
fun ExampleComponent(
    modifier: Modifier = Modifier,
    count: Int,
    example: String
){

    Row(modifier) {

        Box(modifier = Modifier
            .size(24.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp)
            )){
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = count.toString(), style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))
        }

        Text(
            modifier = Modifier.padding(top = 4.dp, start = 8.dp),
            text = example, style = fontMedium14(MaterialTheme.colorScheme.onBackground))

    }

}