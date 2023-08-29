package com.ntg.mywords.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.ui.theme.fontRegular14
import com.ntg.mywords.util.timber

@Composable
fun ItemOption(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    text: String,
    divider: Boolean = true,
    loading: MutableState<Boolean> = remember { mutableStateOf(false) },
    onClick:()-> Unit
) {

    val localDensity = LocalDensity.current

    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }


    timber("kaljdlkwjdkljwalkdjwlakdj $loading")

    Box(modifier = modifier){

        if (loading.value){
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(columnHeightDp)
                .clip(RoundedCornerShape(4.dp)))
        }


        Column(modifier = Modifier
            .onGloballyPositioned { coordinates ->
                columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
            }) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .fillMaxWidth()
                    .clickable {
                        onClick.invoke()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (painter != null){
                    Spacer(modifier = Modifier.padding(start = 32.dp))
                }else{
                    Spacer(modifier = Modifier.padding(start = 8.dp))

                }

                if (painter != null){
                    Icon(
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                            .padding(end = 8.dp)
                            .padding(vertical = 12.dp), painter = painter, contentDescription = "ic"
                    )
                }

                Text(modifier= Modifier
                    .padding(end = 32.dp)
                    .padding(vertical = 12.dp),text = text, style = fontRegular14(MaterialTheme.colorScheme.onBackground))
            }

            if (divider){
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (painter != null) 32.dp else 0.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }

        }


    }

}