package com.ntg.vocabs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.ui.theme.fontMedium12

@Composable
fun AccountState(
    modifier: Modifier,
    isFree: Boolean,
    upgradeClick:() -> Unit = {}
){

    if (isFree){
        Column(modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(modifier = Modifier.padding(top = 16.dp), text = stringResource(id = R.string.free_account), style = fontMedium12(
                MaterialTheme.colorScheme.onSurfaceVariant)
            )
            CustomButton(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), text = stringResource(id = R.string.upgrade_to_pro), size = ButtonSize.LG){
                upgradeClick.invoke()
            }
        }
    }else{
        Column(modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Icon(modifier = Modifier.padding(top = 16.dp).size(42.dp),painter = painterResource(id = R.drawable.v_pro), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            
            Text(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp), text = stringResource(id = R.string.pro_account), style = fontMedium12(MaterialTheme.colorScheme.onSurfaceVariant))

        }
    }
}