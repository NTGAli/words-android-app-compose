package com.ntg.mywords.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.TypewriterText
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.nav.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navController)
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController) {


    Column(modifier = Modifier.padding(horizontal = 32.dp)) {

        TypewriterText(
            modifier = Modifier.padding(top = 64.dp),
            texts = listOf(stringResource(id = R.string.no_list_message)),
            singleText = true,
            speedType = 10L
        )

        CustomButton(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(id = R.string.add_new),
            style = ButtonStyle.TextOnly,
            type = ButtonType.Primary
        ) {
            navController.navigate(Screens.SelectLanguageScreen.name)
        }
    }

}