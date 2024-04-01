package com.ntg.vocabs.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontMedium16

@Composable
fun BackupOption(
    modifier: Modifier,
    title: String,
    subTitle: String? = null,
    onClick:() -> Unit,
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(
            width = 2.dp,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        .clickable {
            onClick.invoke()
        }) {
        Column(modifier = Modifier.align(Alignment.CenterStart))
        {



            Text(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = if (subTitle == null) 16.dp else 0.dp)
                    .padding(horizontal = 16.dp), text = title, style = fontMedium14(
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

          if (subTitle != null){
              Text(
                  modifier = Modifier
                      .padding(horizontal = 16.dp)
                      .padding(bottom = 16.dp, top = 8.dp),
                  text = subTitle,
                  style = fontMedium12(MaterialTheme.colorScheme.outline)
              )
          }

        }
        
        
        Icon(
            modifier = Modifier.align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            imageVector = Icons.Rounded.ChevronRight, contentDescription = null)
        
    }
}