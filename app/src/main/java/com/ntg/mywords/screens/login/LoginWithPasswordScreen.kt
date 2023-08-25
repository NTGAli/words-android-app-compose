package com.ntg.mywords.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.EditText
import com.ntg.mywords.components.TypewriterText
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.util.CountDownTimer
import com.ntg.mywords.util.minutesToTimeFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWithPasswordScreen(navController: NavController, email: String) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navController = navController, email)
//            Content(paddingValues = innerPadding, navController = navController)
        }
    )
}


@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController, email: String) {
    val code = remember {
        mutableStateOf("")
    }


    val loading = remember {
        mutableStateOf(false)
    }

    val setError = remember {
        mutableStateOf(false)
    }




    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),

        ) {

        TypewriterText(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 64.dp),
            texts = listOf(
                "password",
                "رمز"
            ),
            cursor = "\uD83D\uDD12",
        )


        EditText(
            modifier = Modifier
                .padding(top = 64.dp)
                .fillMaxWidth(), label = stringResource(id = R.string.password), text = code,
            setError = setError,
            supportText = stringResource(id = R.string.set_password, email)
        )


        CustomButton(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(id = R.string.next),
            type = ButtonType.Primary,
            size = ButtonSize.LG,
            loading = loading.value
        ) {
            loading.value = true

            navController.navigate(Screens.NameScreen.name)

//            if (email.value.validEmail()) {
//                navController.navigate(Screens.CodeScreen.name)
//            } else {
//                setError.value = true
//            }


        }
        
        
        CustomButton(modifier = Modifier.padding(top = 8.dp), text = stringResource(id = R.string.use_verification_code), type = ButtonType.Primary, style = ButtonStyle.TextOnly, size = ButtonSize.LG){
//            navController.navigate(Screens.CodeScreen.name)
            navController.popBackStack()
        }


    }
}
