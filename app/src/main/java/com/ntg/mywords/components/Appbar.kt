package com.ntg.mywords.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
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
    enableSearchbar: MutableState<Boolean> = remember { mutableStateOf(false) },
    actions: List<AppbarItem> = emptyList(),
    popupItems: List<PopupItem> = emptyList(),
    actionOnClick: (Int) -> Unit = {},
    popupItemOnClick: (Int) -> Unit = {},
    onQueryChange: (String) -> Unit = {}
) {
    if (enableSearchbar.value) {
        SearchBar(
            onQueryChange = { onQueryChange.invoke(it) },
            onDismiss = { enableSearchbar.value = false })
    } else {
        Column(modifier = modifier) {

            TopAppBar(
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        style = fontBold14(MaterialTheme.colorScheme.onBackground)
                    )
                },
                navigationIcon = {
                    if (enableNavigation) {

                        IconButton(onClick = { navigationOnClick.invoke() }) {
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
                        Popup(popupItems = popupItems) {
                            popupItemOnClick.invoke(it)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
//                        MaterialTheme.colorScheme.background
                ),
                scrollBehavior = scrollBehavior,
                windowInsets = TopAppBarDefaults.windowInsets
            )

            if (scrollBehavior?.state?.contentOffset.orZero() < -25f) {
                Divider(Modifier.height(1.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            }

        }

    }


}

@Composable
fun Popup(modifier: Modifier = Modifier, popupItems: List<PopupItem>, onClick: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                tint = Secondary500,
                contentDescription = "action appbar"
            )
        }



        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {

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
                            Text(it.title, style = fontRegular14(MaterialTheme.colorScheme.outline))
                        },
                        leadingIcon = {
                            Icon(
                                painter = it.icon,
                                contentDescription = it.title,
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    )
                }


            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(onQueryChange: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current



    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = text,
        onValueChange = {
            text = it
            onQueryChange.invoke(it)
        },
        singleLine = true,
        textStyle = fontRegular14(MaterialTheme.colorScheme.onSurfaceVariant),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        colors = TextFieldDefaults.textFieldColors(
//            cursorColor = Primary500,
//            focusedLeadingIconColor = Secondary700,
//            containerColor = Color.White,
//            focusedTextColor = Secondary800,
//            unfocusedTextColor = Secondary500,
//            focusedIndicatorColor = Primary500,
//            unfocusedIndicatorColor = Secondary500
        ),
        trailingIcon = {
            IconButton(onClick = {
                onQueryChange("")
                onDismiss()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "leading",
                    tint = Secondary500
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}