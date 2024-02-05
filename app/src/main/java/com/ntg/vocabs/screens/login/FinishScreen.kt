package com.ntg.vocabs.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.nav.Screens

@Composable
fun FinishScreen(navController: NavController){

    Column(modifier = Modifier
        .padding(horizontal = 32.dp)
        .padding(top = 64.dp)) {
        
        TypewriterText(texts = listOf("We are sorry that we could not meet your needs.\uD83E\uDD72 \n\n We will be happy if you come back\uD83D\uDE09"), singleText = true)

        CustomButton(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), text = stringResource(id = R.string.finish)){
            navController.navigate(Screens.InsertEmailScreen.name) {
                popUpTo(0)
            }
        }
    }

}