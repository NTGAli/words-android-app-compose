package com.ntg.mywords.components

import android.content.res.Configuration
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.mywords.R
import com.ntg.mywords.ui.theme.fontRegular12

@Composable
fun SimpleBtn(
    modifier: Modifier = Modifier,
    title: String,
    painter: Painter,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                onClick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 8.dp)
                .padding(vertical = 16.dp),
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.inverseSurface
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = title,
            style = fontRegular12(MaterialTheme.colorScheme.inverseSurface)
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SimpleBtnPreview() {
    SimpleBtn(
        title = "Send feedback",
        painter = painterResource(id = R.drawable.message_circle)
    ) {

    }
}