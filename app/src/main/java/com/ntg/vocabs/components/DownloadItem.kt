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

@Composable
fun DownloadItem(
    modifier: Modifier = Modifier,
    id: Int,
    title: String,
    subTitle: String,
    tertiaryText: String? = null,
    isSelected: Boolean,
    isSEnable: Boolean,
    downloadProgress: Int? = null,
    onClick: (Int) -> Unit,
) {

    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(
            width = 2.dp,
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
        .wrapContentHeight()
        .clickable(enabled = isSEnable) {
            onClick.invoke(id)
        }) {
        Column(modifier = Modifier.align(Alignment.CenterStart))
        {

            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp), text = title, style = fontMedium16(
                    MaterialTheme.colorScheme.primary
                )
            )

            Text(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = if (tertiaryText == null) 16.dp else 8.dp)
                    .padding(horizontal = 16.dp), text = subTitle, style = fontMedium14(
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            if (tertiaryText != null) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    text = tertiaryText,
                    style = fontMedium12(MaterialTheme.colorScheme.outline)
                )
            }

        }


        if (downloadProgress != null && downloadProgress != 0) {

            if (downloadProgress == 100 || downloadProgress == -1){
                Icon(
                    modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
                    painter = if (downloadProgress == 100) painterResource(id = R.drawable.check_mark_rectangle) else painterResource(id = R.drawable.redo),
                    contentDescription = null,
                    tint = if (downloadProgress == 100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }else{
                Text(
                    modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
                    text = stringResource(id = R.string.progress_format, downloadProgress.toString()),
                    style = fontMedium14(MaterialTheme.colorScheme.primary)
                )
            }

        } else {
            Icon(
                modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                painter = if (isSEnable) painterResource(id = R.drawable.folder_download) else painterResource(id = R.drawable.check_mark_rectangle),
                contentDescription = null
            )
        }


    }


}