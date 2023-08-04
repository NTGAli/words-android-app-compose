package com.ntg.mywords.model.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.mywords.R
import com.ntg.mywords.ui.theme.Primary500
import com.ntg.mywords.ui.theme.Secondary800
import com.ntg.mywords.ui.theme.fontMedium12
import com.ntg.mywords.ui.theme.fontRegular12

@Composable
fun TextWithContext(
    modifier: Modifier = Modifier,
    title: String,
    description: String
){

    Row {
        Text(text = stringResource(id = R.string.context_colon_form, title), style = fontRegular12(Secondary800))
        Text(modifier= Modifier.padding(start = 2.dp),text = description, style = fontRegular12(Primary500))

    }

}