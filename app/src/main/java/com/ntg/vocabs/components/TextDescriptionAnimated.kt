package com.ntg.vocabs.components

import android.text.SpannableStringBuilder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.ntg.vocabs.ui.theme.fontMedium16
import com.ntg.vocabs.ui.theme.fontRegular14
import com.ntg.vocabs.util.toAnnotatedString

@Composable
fun TextDescriptionAnimated(
    modifier: Modifier = Modifier,
    text: String,
    description: String
){
    var visible by remember {
        mutableStateOf(false)
    }

    val spannableString = SpannableStringBuilder(description).toString()
    val spanned = HtmlCompat.fromHtml(spannableString, HtmlCompat.FROM_HTML_MODE_COMPACT)

    Column(modifier = modifier
        .clickable {
            visible = !visible
        }) {
        Text(text = text, style = fontMedium16(MaterialTheme.colorScheme.inverseSurface))

        AnimatedVisibility(visible = visible) {
            Text(modifier = Modifier.padding(top = 8.dp),text = spanned.toAnnotatedString(), style = fontRegular14(MaterialTheme.colorScheme.onSurfaceVariant))

        }
    }

}