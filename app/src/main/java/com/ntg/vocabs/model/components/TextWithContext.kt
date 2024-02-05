package com.ntg.vocabs.model.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.R
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium14

@Composable
fun TextWithContext(
    modifier: Modifier = Modifier,
    title: String,
    description: String
){

    Row {
        Text(text = stringResource(id = R.string.context_colon_form, title), style = fontMedium12(MaterialTheme.colorScheme.onSurfaceVariant))
        Text(modifier= Modifier.padding(start = 2.dp),text = description, style = fontMedium14(MaterialTheme.colorScheme.onSurface))

    }

}