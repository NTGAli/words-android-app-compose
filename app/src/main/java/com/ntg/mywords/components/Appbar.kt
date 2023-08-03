package com.ntg.mywords.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.AppbarItem
import com.ntg.mywords.model.components.PopupItem
import com.ntg.mywords.ui.theme.*
import com.ntg.mywords.util.orZero

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appbar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "Appbar",
    titleColor: Color = Secondary900,
    color: Color = Color.White,
    enableNavigation: Boolean = true,
    navigationOnClick: () -> Unit = {},
    navigateIconColor: Color = Secondary500,
    actions: List<AppbarItem> = emptyList(),
    popupItems: List<PopupItem> = emptyList(),
    actionOnClick: (Int) -> Unit = {},
    popupItemOnClick: (Int) -> Unit = {}
) {

    Column(modifier = modifier) {

        TopAppBar(
            title = {
                Text(
                    title,
                    maxLines = 1,
                    style = FontBold14(titleColor)
                )
            },
            navigationIcon = {
                if (enableNavigation) {

                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowLeft,
                            contentDescription = "navigation",
                            tint = navigateIconColor
                        )
                    }

                }

            },
            actions = {
                actions.forEach { appbarItem ->
                    IconButton(onClick = { actionOnClick.invoke(appbarItem.id) }) {
                        Icon(
                            imageVector = appbarItem.imageVector,
                            tint = appbarItem.iconColor,
                            contentDescription = "action appbar"
                        )
                    }
                }

                if (popupItems.isNotEmpty()) {
                    Popup(popupItems = popupItems){
                        popupItemOnClick.invoke(it)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                color
            ),
            scrollBehavior = scrollBehavior
        )

        if (scrollBehavior?.state?.contentOffset.orZero() < -25f) {
            Divider(Modifier.height(1.dp), color = Secondary100)
        }

    }


}

@Composable
fun Popup(popupItems: List<PopupItem>, onClick:(Int) -> Unit){
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = {expanded = true}
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                tint = Secondary500,
                contentDescription = "action appbar"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            popupItems.forEach {

                DropdownMenuItem(
                    onClick = {
                        onClick.invoke(it.id)
                        expanded = false
                    },
                    interactionSource = MutableInteractionSource(),
                    text = {
                        Text(it.title, style= fontRegular14(Secondary500))
                    },
                    leadingIcon = {
                        Icon(painter = it.icon, contentDescription = it.title, tint = Secondary700 )
                    }
                )

            }


        }
    }
}