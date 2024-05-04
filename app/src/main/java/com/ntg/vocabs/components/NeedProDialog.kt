package com.ntg.vocabs.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ntg.vocabs.R

@Composable
fun NeedProDialog(
    type: DescriptionType,
    onClick:()-> Unit,
    onDismiss:()->Unit
){

    val description = when(type){
        DescriptionType.IMAGE -> {
            stringResource(id = R.string.vocabs_master_plus_description)
        }

        DescriptionType.TIME -> {
            stringResource(id = R.string.time_plus_description)
        }

        DescriptionType.LIST -> {
            stringResource(id = R.string.list_plus_description)
        }

        DescriptionType.DICTIONARY -> {
            stringResource(id = R.string.dictionary_plus_description)
        }

        else -> {
            stringResource(id = R.string.vocabs_master_plus_description)
        }
    }

    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.vocabs_master_plus))
        },
        text = {
            Text(text = description)
        },
        onDismissRequest = {
            onDismiss.invoke()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClick()
                }
            ) {
                Text("Upgrade")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

enum class DescriptionType{
    IMAGE,
    TIME,
    DICTIONARY,
    BACKUP,
    LIST
}