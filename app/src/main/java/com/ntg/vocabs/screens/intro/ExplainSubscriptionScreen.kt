package com.ntg.vocabs.screens.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.TLButton
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.vm.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplainSubscriptionScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val email = loginViewModel.getUserData().collectAsState(initial = null).value?.email
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                navController = navController)
        },
        bottomBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                Divider(
                    Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                TLButton(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    text = stringResource(id = R.string.upgrade_to_pro)
                ) {
                    if (email.orEmpty().isNotEmpty()){
                        navController.navigate(Screens.SubscriptionsScreen.name)
                    }else{
                        navController.navigate(Screens.GoogleLoginScreen.name + "?skip=${false}")
                    }
                }
                CustomButton(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    text = stringResource(R.string.continue_free),
                    style = ButtonStyle.TextOnly,
                    size = ButtonSize.XL
                ) {
                    loginViewModel.continueFree()
                }
            }
        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
){
    var showSecondText by remember {
        mutableStateOf(false)
    }

    var showThirdText by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        TypewriterText(
            modifier = Modifier.padding(top = 64.dp),
            texts = listOf("Enjoy Vocabs for free with unlimited word additions!\uD83E\uDD29"),
            singleText = true,
            speedType = 8L
        ){
            showSecondText = it
        }

        if (showSecondText){
            TypewriterText(
                modifier = Modifier.padding(top = 16.dp),
                texts = listOf("But\uD83E\uDD28"), singleText = true
            ){
                showThirdText = it
            }
        }

        if (showThirdText){
            TypewriterText(
                modifier = Modifier.padding(top = 16.dp),
                texts = listOf("You can upgrade to the Pro for enhanced features and support us❤\uFE0F. It's a one-time payment only."),
                singleText = true,
                speedType = 8L
            )
        }
    }
}