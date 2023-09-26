package com.ntg.mywords.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.fontRegular12

@Composable
fun BubblePopup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isPopupVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    isPopupVisible = !isPopupVisible
                }
            }
    ) {
        // Content that should always be visible
        content()

        if (isPopupVisible) {
            PopupCard(
                onDismiss = { isPopupVisible = false },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
            ) {
                // Popup content here
                Text(
                    text = "Bubble Popup Content",
                    style = TextStyle(color = Color.Black)
                )
            }
        }
    }
}

@Composable
fun PopupCard(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 0.dp, bottomEnd = 0.dp), // Rectangle on top
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                content()
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDismiss() }
                        .padding(8.dp)
                        .align(Alignment.End)
                )
            }
        }
    )
}

@Composable
fun BubblePopupExample() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        BubblePopup(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Click me to show the popup",
                style = TextStyle(color = Color.White),
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Blue)
                    .clickable { /* Handle clicks on this content */ }
            )
        }
    }
}