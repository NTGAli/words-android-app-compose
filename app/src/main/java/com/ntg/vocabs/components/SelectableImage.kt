package com.ntg.vocabs.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun SelectableImage(
    modifier: Modifier = Modifier,
    path: String,
    onClick:(String) -> Unit
){

    val imageBitmap = loadImageFromFile(filePath = path)
    Box(modifier = modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)).clickable {
                                                                                     onClick.invoke(path)
    }, contentAlignment = Alignment.Center){
        Image(modifier = Modifier.fillMaxSize(),
            bitmap = imageBitmap!!.asImageBitmap()
            , contentDescription = "Selected image", contentScale = ContentScale.Crop)
    }

}