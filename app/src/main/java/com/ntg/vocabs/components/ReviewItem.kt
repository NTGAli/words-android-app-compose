package com.ntg.vocabs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.R
import com.ntg.vocabs.ui.theme.Warning050
import com.ntg.vocabs.ui.theme.Warning100
import com.ntg.vocabs.ui.theme.Warning500
import com.ntg.vocabs.ui.theme.Warning700
import com.ntg.vocabs.ui.theme.Warning800
import com.ntg.vocabs.ui.theme.Warning900
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontMedium16

@Composable
fun ReviewItem(
    modifier: Modifier = Modifier,
    count: Int,
    title: String,
    isPro: Boolean,
    onClick:() -> Unit
){

    Box(modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .border(
            width = 2.dp,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        .clickable {
            onClick.invoke()
        }
        .fillMaxWidth()
    ){

        Column {

            Text(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .background(Warning050, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                text = if (count != -1) stringResource(id = R.string.words_format, count) else stringResource(id = R.string.all_words),
                style = fontMedium14(Warning700)
            )
            
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 6.dp),
                text = title,
                style = fontMedium16(MaterialTheme.colorScheme.onSurfaceVariant)
            )

            if (isPro){
                Row(
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp, start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.drawable.icons8_clock_1_1), contentDescription = "lock")
                    Text(modifier = Modifier.padding(start = 4.dp),text = stringResource(id = R.string.pro_users), style = fontMedium12(MaterialTheme.colorScheme.outline))
                }
            }else{
                Spacer(modifier = Modifier.padding(8.dp))
            }
            
        }

    }


}