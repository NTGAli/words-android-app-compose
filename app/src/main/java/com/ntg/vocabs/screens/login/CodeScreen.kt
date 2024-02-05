package com.ntg.vocabs.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.EditText
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.model.enums.TypeOfVerifyCode
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.util.CountDownTimer
import com.ntg.vocabs.util.minutesToTimeFormat
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeScreen(navController: NavController, email: String, loginViewModel: LoginViewModel) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navController = navController, email, loginViewModel)
//            Content(paddingValues = innerPadding, navController = navController)
        }
    )
}


@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController, email: String, loginViewModel: LoginViewModel) {

    val owner = LocalLifecycleOwner.current
    val context = LocalContext.current

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
    var ticks by remember { mutableStateOf(120) }

    if (waiting.value){
        CountDownTimer(start = 120, onTick = {
            ticks = it
            if (ticks == 0){
                waiting.value = false
            }
        })
    }

    if (code.value.length == 4 && !loading.value){
        loading.value = true
        loginViewModel.verifyUserByCode(
            code = code.value,
            email = email
        ).observe(owner){
            when(it){
                is NetworkResult.Error -> {
                    loading.value = false
                    code.value = ""
                    context.toast(context.getString(R.string.sth_wrong))
                    timber("verifyCode ::: ERR :: ${it.message}")
                }
                is NetworkResult.Loading -> {
                    timber("verifyCode ::: LD")
                }
                is NetworkResult.Success -> {
                    loading.value = false
                    code.value = ""

                    when(it.data){

                        TypeOfVerifyCode.ALREADY_VERIFIED.name -> {
                            context.toast(context.getString(R.string.already_verified))
                        }

                        TypeOfVerifyCode.USER_VERIFIED_NO_NAME.name -> {
                            loginViewModel.setUserEmail(email)
                            navController.navigate(Screens.NameScreen.name+ "?email=${email}"){
                                popUpTo(0)
                            }
                        }

                        TypeOfVerifyCode.CODE_EXPIRED.name -> {
                            context.toast(context.getString(R.string.your_code_expired))
                        }

                        TypeOfVerifyCode.INCORRECT_CODE.name -> {
                            context.toast(context.getString(R.string.incorrect_code))
                        }

                        TypeOfVerifyCode.INVALID_TOKEN.name -> {
                            context.toast(context.getString(R.string.download_from_google_play))
                        }

                        TypeOfVerifyCode.ERR.name -> {
                            context.toast(context.getString(R.string.sth_wrong))
                        }

                        else -> {
                            loginViewModel.setUserEmail(it.data.orEmpty())
                            loginViewModel.setUserEmail(email)
                            navController.navigate(Screens.VocabularyListScreen.name){
                                popUpTo(0)
                            }
                        }

                    }
                }
            }

        }
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
                "コード",
                "Código",
                "Codice",
                "شفرة",
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
            modifier = Modifier.padding(top = 32.dp)
                .fillMaxWidth(),
            text = if (waiting.value) ticks.minutesToTimeFormat() else stringResource(id = R.string.send_again),
            type = if (waiting.value) ButtonType.Secondary else ButtonType.Primary,
            size = ButtonSize.LG,
            enable = !waiting.value,
            loading = loading.value
        ) {
            loading.value = true




        }

        CustomButton(modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), text = stringResource(id = R.string.use_password), type = ButtonType.Primary, style = ButtonStyle.TextOnly, size = ButtonSize.LG){
            navController.navigate(Screens.LoginWithPasswordScreen.name+"?email=$email")
        }


    }
}
