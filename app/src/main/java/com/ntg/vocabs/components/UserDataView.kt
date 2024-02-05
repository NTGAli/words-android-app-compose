package com.ntg.vocabs.components

import com.ntg.vocabs.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.UserDataAndSetting
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontRegular12

@Composable
fun UserDataView(
    modifier: Modifier = Modifier,
    userDataAndSetting: UserDataAndSetting?,
    editNameClick:() -> Unit,
    loginOnClick:() -> Unit
){

    if (userDataAndSetting?.email.orEmpty().isNotEmpty()){
        Box(modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)){

            Column {
                Row (modifier = Modifier.padding(start = 16.dp, top = 16.dp)){
                    Text(modifier = Modifier.padding(end = 8.dp),text = stringResource(id = R.string.hey_format, userDataAndSetting?.name ?: "no one"), style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))
                    IconButton(modifier = Modifier.size(16.dp),onClick = { editNameClick.invoke() }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "edit name",modifier = Modifier.size(12.dp))
                    }
                }
                Text(modifier = Modifier.padding(top = 8.dp, start = 16.dp, bottom = 16.dp), text = userDataAndSetting?.email.orEmpty(), style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))

            }

        }
    }else{
        
        Column(modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally) {

            Text(modifier = Modifier.padding(top = 16.dp), text = stringResource(id = R.string.havent_logged), style = fontMedium12(MaterialTheme.colorScheme.onSurfaceVariant))
            CustomButton(modifier = Modifier.fillMaxWidth().padding(16.dp), text = stringResource(id = R.string.login), size = ButtonSize.LG){
                loginOnClick.invoke()
            }
        }
        
    }


}