package com.ntg.mywords.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
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
import com.ntg.mywords.util.timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeScreen(navController: NavController, email: String) {
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

    val waiting = remember {
        mutableStateOf(true)
    }

    val loading = remember {
        mutableStateOf(false)
    }

    val setError = remember {
        mutableStateOf(false)
    }
    var ticks by remember { mutableStateOf(5) }

    if (waiting.value){
        CountDownTimer(start = 2, onTick = {
            ticks = it
            if (ticks == 0){
                waiting.value = false
            }
        })
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
                "Code",
                "کد"
            ),
            cursor = "\uD83D\uDD12",
        )


        EditText(
            modifier = Modifier
                .padding(top = 64.dp)
                .fillMaxWidth(), label = stringResource(id = R.string.code), text = code,
            setError = setError,
            supportText = stringResource(id = R.string.send_code_to_format, email)
        )


        CustomButton(
            modifier = Modifier.padding(top = 32.dp),
            text = if (waiting.value) ticks.minutesToTimeFormat() else stringResource(id = R.string.send_again),
            type = if (waiting.value) ButtonType.Secondary else ButtonType.Primary,
            size = ButtonSize.LG,
            enable = !waiting.value,
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

        CustomButton(modifier = Modifier.padding(top = 8.dp), text = stringResource(id = R.string.use_password), type = ButtonType.Primary, style = ButtonStyle.TextOnly, size = ButtonSize.LG){
            navController.navigate(Screens.LoginWithPasswordScreen.name+"?email=$email")
        }


    }
}
