package com.ntg.vocabs.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditText(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: MutableState<String> = remember { mutableStateOf("") },
    setError: MutableState<Boolean> = remember { mutableStateOf(false) },
    supportText: String = "",
    label: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    color: TextFieldColors? = null,
    leadingIcon: ImageVector = Icons.Rounded.Add,
    enabledLeadingIcon: Boolean = false,
    leadingIconOnClick: (String) -> Unit = {},
    leftIconOnClick: (String) -> Unit = {},
    searchMode: Boolean = false,
    onClick: () -> Unit = {},
    onChange: (String) -> Unit = {}

) {


    var passwordVisible by rememberSaveable { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = modifier
            .width(2.dp)
            .clickable {
                onClick.invoke()
            }
            .focusRequester(focusRequester), value = text.value,
        onValueChange = {
            text.value = it
            onChange.invoke(it)
        },
        label = if (!searchMode) {
            {
                if (!label.isNullOrEmpty()) {
                    Text(
                        text = label, maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else null,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        readOnly = readOnly,
        textStyle = fontRegular14(MaterialTheme.colorScheme.onSurfaceVariant),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            if (enabledLeadingIcon) {
                IconButton(onClick = {
                    leadingIconOnClick.invoke(text.value)
                }) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = "leading"
                    )
                }
            } else if (isPassword) {
                val image = if (passwordVisible)
                    Icons.Rounded.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }


        },
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            onClick.invoke()
                        }
                    }
                }
            }, isError = setError.value,
        supportingText = if (supportText.isNotEmpty()) {
            {

//            if (supportText.isNotEmpty()) {
                Text(text = supportText)
//            }
            }
        } else null,
        colors = if (searchMode) {
            TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.background
            )
        } else OutlinedTextFieldDefaults.colors(),
        singleLine = searchMode,
        leadingIcon = if (searchMode) {
            {
                IconButton(onClick = {
                    leftIconOnClick.invoke(text.value)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = "leading"
                    )
                }
            }

        } else null

    )

    LaunchedEffect(Unit) {
        if (searchMode) {
            focusRequester.requestFocus()
        }
    }


}