package com.ntg.mywords.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.mywords.R
import com.ntg.mywords.ui.theme.Success500
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.ui.theme.fontRegular12
import com.ntg.mywords.ui.theme.fontRegular14
import com.ntg.mywords.util.timber

@Composable
fun ItemOption(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    text: String,
    subText: String? = null,
    divider: Boolean = true,
    endIcon: Painter? = null,
    visibleWithAnimation: MutableState<Boolean> = remember { mutableStateOf(false) },
    loading: MutableState<Boolean> = remember { mutableStateOf(false) },
    onClick: () -> Unit
) {

    val localDensity = LocalDensity.current

    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }



    Box(modifier = modifier) {

        if (loading.value) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(columnHeightDp)
                    .clip(RoundedCornerShape(4.dp))
            )
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

                if (painter != null) {
                    Spacer(modifier = Modifier.padding(start = 32.dp))
                } else {
                    Spacer(modifier = Modifier.padding(start = 8.dp))

                }

                if (painter != null) {
                    Icon(
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                            .padding(end = 8.dp)
                            .padding(vertical = 12.dp), painter = painter, contentDescription = "ic"
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 16.dp)
                        .padding(end = 32.dp)
                ) {

                    Text(
                        text = text,
                        style = fontRegular14(MaterialTheme.colorScheme.onBackground)
                    )

                    if (subText != null) {
                        Text(
                            text = subText,
                            style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }


                if (endIcon != null) {

                    AnimatedVisibility(
                        visible = visibleWithAnimation.value,
                        enter = fadeIn(
                            // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                            initialAlpha = 0.4f
                        ),
                        exit = fadeOut(
                            // Overwrites the default animation with tween
                            animationSpec = tween(durationMillis = 250)
                        )
                    ) {
                        Icon(
                            modifier = modifier.padding(end = 8.dp),
                            painter = painterResource(id = R.drawable.ok),
                            tint = Success500,
                            contentDescription = "rightIcon"
                        )
                    }

                }
            }



            if (divider) {
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