package com.ntg.vocabs.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontMedium16
import com.ntg.vocabs.R
import com.ntg.vocabs.util.unixTimeToReadable

@Composable
fun BackupReportItem(
    modifier: Modifier = Modifier,
    id: Int,
    subTitle: String,
    tertiaryText: String,
    isSuccess: Boolean,
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart))
        {

            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp), text = if (isSuccess) stringResource(id = R.string.success) else stringResource(
                    id = R.string.failed
                ), style = fontMedium16(
                    if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            )

            Text(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
                    .padding(horizontal = 16.dp), text = subTitle.toLong().unixTimeToReadable(), style = fontMedium14(
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

//            Text(
//                modifier = Modifier
//                    .padding(horizontal = 16.dp)
//                    .padding(bottom = 16.dp),
//                text = tertiaryText,
//                style = fontMedium12(MaterialTheme.colorScheme.outline)
//            )

        }
        Icon(
            modifier =
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            painter = if (isSuccess) painterResource(id = R.drawable.tick) else painterResource(id = R.drawable.remove),
            contentDescription = null
        )


    }


}