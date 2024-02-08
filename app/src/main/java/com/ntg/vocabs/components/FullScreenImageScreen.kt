package com.ntg.vocabs.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.vm.MessageBoxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenImageScreen(
    navController: NavController,
    imagePath: String? = null
){
    if (imagePath == null) navController.popBackStack()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, imagePath!!)
        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    imagePath: String
){
    val imageBitmap = loadImageFromFile(filePath = imagePath)
    // set up all transformation states
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    Box(
        Modifier
            .padding(paddingValues)
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .fillMaxSize()
    ){
        Image(modifier = Modifier.fillMaxSize(), bitmap = imageBitmap!!.asImageBitmap(), contentDescription = null)
    }
}