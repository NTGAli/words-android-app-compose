package com.ntg.vocabs.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.ntg.vocabs.ui.theme.Success500
import com.ntg.vocabs.ui.theme.fontRegular14

@Composable
fun SimpleReviewItem(
    modifier: Modifier = Modifier,
    text: String,
    isCorrect: Boolean? = null,
    clickEnabled: Boolean = true,
    onClick: (String) -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = clickEnabled) {
                onClick.invoke(text)
            }
            .border(
                width = 1.dp,
                color = if (isCorrect != null) {if (isCorrect) Success500 else MaterialTheme.colorScheme.error} else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(start = 24.dp),
            text = text,
            style = fontRegular14(MaterialTheme.colorScheme.onBackground)
        )
    }

}