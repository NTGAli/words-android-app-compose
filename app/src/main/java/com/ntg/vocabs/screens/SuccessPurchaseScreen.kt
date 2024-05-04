package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.vm.LoginViewModel

@Composable
fun SuccessPurchaseScreen(
    navController: NavController,
    type: String
){

    Scaffold(
        bottomBar = {
            CustomButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).padding(bottom = 32.dp),
                text = stringResource(id = R.string.continue_str), size = ButtonSize.XL){
                navController.navigate(Screens.VocabularyListScreen.name){
                    popUpTo(0)
                }
            }
        }
    ) {
        Content(
            it,
            type = type)
    }

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    type: String
){

    val text = if (type == "Purchase"){
        listOf(stringResource(id = R.string.thanks_purchase))
    }else{
        listOf(stringResource(id = R.string.success_restore))
    }

    TypewriterText(
        modifier = Modifier
            .padding(top = 64.dp)
            .padding(horizontal = 32.dp),
        texts = text, singleText = true)
    
}