package com.ntg.mywords.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.ui.theme.AppTheme
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.ui.theme.fontRegular12

@Composable
fun Message(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    subTitle: String? = null,
    btnText: String? = null,
    btnLoading: Boolean = false,
    btnClick:() -> Unit = {}
){

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {


        Row(modifier = Modifier
            .padding(vertical = 16.dp,horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier
                .weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(modifier = Modifier.size(12.dp), painter = icon, contentDescription = "icon")
                    Text(modifier = Modifier.padding(start = 8.dp), text = title, style = fontMedium14(MaterialTheme.colorScheme.onTertiaryContainer))
                }
                if (subTitle != null){
                    Text(modifier= Modifier.padding(top = 4.dp),text = subTitle, style = fontRegular12(MaterialTheme.colorScheme.onTertiaryContainer))
                }
            }
            if (btnText != null){
                CustomButton(modifier = Modifier.wrapContentSize(), text = btnText, style = ButtonStyle.Outline, size = ButtonSize.XS, loading = btnLoading){
                    btnClick.invoke()
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        Message(icon = painterResource(id = com.ntg.mywords.R.drawable.download), title = "your backup is available", subTitle = "last backup: 11 Des 2023")
    }
}