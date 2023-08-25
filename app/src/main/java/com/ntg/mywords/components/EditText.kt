package com.ntg.mywords.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.*

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
    color: TextFieldColors? = null,
    leadingIcon: ImageVector = Icons.Rounded.Add,
    enabledLeadingIcon: Boolean = false,
    leadingIconOnClick:(String) -> Unit = {},
    onClick: () -> Unit = {},
    onChange: (String) -> Unit = {}

) {


    OutlinedTextField(
        modifier = modifier
            .width(2.dp)
            .clickable {
                onClick.invoke()
            }, value = text.value,
        onValueChange = {
            text.value = it
            onChange.invoke(it)
        },
        label = {
            if (!label.isNullOrEmpty()) {
                Text(text = label)
            }
        },
        readOnly = readOnly,
        textStyle = fontRegular14(MaterialTheme.colorScheme.onSurfaceVariant),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            if (enabledLeadingIcon){
                IconButton(onClick = {
                    leadingIconOnClick.invoke(text.value)
                }) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = "leading"
                    )
                }
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
//            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
//            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
//            focusedBorderColor = Primary500,
//            unfocusedBorderColor = Secondary700,
//            focusedLabelColor = FocusLabel,
//            cursorColor = Primary500,
//            focusedLeadingIconColor = Danger050
        ),
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
        supportingText = {
            Text(text = supportText)
        }

    )


}