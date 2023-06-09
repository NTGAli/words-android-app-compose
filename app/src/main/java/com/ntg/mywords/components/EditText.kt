package com.ntg.mywords.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.*
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditText(
    modifier: Modifier = Modifier,
    text: MutableState<String> = remember { mutableStateOf("") },
    label: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    color: TextFieldColors? = null,
    enabledLeadingIcon: Boolean = false,
    leadingIconOnClick:(String) -> Unit = {},
    onClick: () -> Unit = {}

) {


    OutlinedTextField(
        modifier = modifier
            .width(2.dp)
            .clickable {
                onClick.invoke()
            }, value = text.value,
        onValueChange = {
            text.value = it
        },
        label = {
            if (!label.isNullOrEmpty()) {
                Text(text = label, style = FontRegular14(Secondary500))
            }
        },
        readOnly = readOnly,
        textStyle = FontRegular14(Secondary900),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            if (enabledLeadingIcon){
                IconButton(onClick = {
                    leadingIconOnClick.invoke(text.value)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "leading"
                    )
                }
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Primary500,
            unfocusedBorderColor = Secondary700,
            focusedLabelColor = FocusLabel,
            cursorColor = Primary500,
            focusedLeadingIconColor = Danger050
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
            }

    )


}