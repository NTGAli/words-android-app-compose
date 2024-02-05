package com.ntg.vocabs.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.R
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.ui.theme.fontRegular12


@Composable
fun ButtonIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    bitmap: Bitmap? = null,
    subText: String? = null,
    removeBtn:Boolean = false,
    enable: Boolean = true,
    removeOnClick:() -> Unit = {},
    onCLick:() -> Unit ={},
){


    Column(modifier = modifier) {

        Box(modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            .fillMaxWidth()
            .clickable(enabled = enable) {
                onCLick.invoke()
            },
            contentAlignment = Alignment.Center){

            if (bitmap == null){
                Icon(modifier = Modifier
                    .padding(vertical = 16.dp)
                    .alpha(
                        if (enable) 1f else 0.5f
                    ),painter = painterResource(id = icon), contentDescription = null)
            }else{
                Box(modifier = Modifier.aspectRatio(4f), contentAlignment = Alignment.Center){
                    Image(modifier = Modifier.fillMaxSize(),bitmap = bitmap.asImageBitmap(), contentDescription = "Selected image", contentScale = ContentScale.Crop)
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center){
                        Icon(painter = painterResource(id = R.drawable.remove_rectangle), contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }

            }



        }

        if (subText != null){
            Text(text = subText, style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))
        }

        if (removeBtn){
            CustomButton(text = stringResource(id = R.string.remove_audio), type = ButtonType.Danger, style = ButtonStyle.TextOnly, size = ButtonSize.SM){
                removeOnClick.invoke()
            }
        }
    }




}
