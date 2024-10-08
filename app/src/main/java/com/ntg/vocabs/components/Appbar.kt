package com.ntg.vocabs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.R
import com.ntg.vocabs.model.components.AppbarItem
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.PopupItem
import com.ntg.vocabs.ui.theme.*
import com.ntg.vocabs.util.orZero

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appbar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "",
    titleState: @Composable () -> Unit = {},
    buttonText: String? = null,
    endText: String? = null,
    titleColor: Color = Secondary900,
    color: Color = Color.White,
    enableNavigation: Boolean = true,
    navigationOnClick: () -> Unit = {},
    navigateIconColor: Color = Secondary500,
    enableSearchbar: MutableState<Boolean> = remember { mutableStateOf(false) },
    searchQueryText: MutableState<String> = remember { mutableStateOf("") },
    actions: List<AppbarItem> = emptyList(),
    popupItems: List<PopupItem>? = null,
    actionOnClick: (Int) -> Unit = {},
    popupItemOnClick: (Int) -> Unit = {},
    onQueryChange: (String) -> Unit = {}
) {
    if (enableSearchbar.value) {
        SearchBar(
            searchQueryText,
            onQueryChange = { onQueryChange.invoke(it) },
            onDismiss = { enableSearchbar.value = false })
    } else {
        Column(modifier = modifier) {

            TopAppBar(
                title = {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            title,
                            maxLines = 1,
                            style = fontBold14(MaterialTheme.colorScheme.onBackground)
                        )

                        titleState()

                    }

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

                    if (popupItems != null) {
                        Popup(popupItems = popupItems) {
                            popupItemOnClick.invoke(it)
                        }
                    }

                    if (buttonText != null) {
                        CustomButton(
                            text = buttonText,
                            style = ButtonStyle.TextOnly,
                            size = ButtonSize.SM
                        ) {
                            actionOnClick.invoke(0)
                        }
                    }

                    if (endText != null){
                        Text(
                            modifier = Modifier.padding(end = 24.dp),
                            text = endText,
                            style = fontMedium12(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.background
                )
                ,
//                scrollBehavior = scrollBehavior,
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
            onClick = {
                expanded = true
                onClick.invoke(-1)
            }
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
fun SearchBar(
    searchQueryText: MutableState<String> = remember { mutableStateOf("") },
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(searchQueryText.value) }

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