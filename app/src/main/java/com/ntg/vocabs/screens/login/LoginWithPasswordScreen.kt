package com.ntg.vocabs.screens.login

import com.ntg.vocabs.R
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.actionCodeSettings
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.EditText
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.Failure
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.model.enums.TypeOfMessagePass
import com.ntg.vocabs.model.then
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.util.*
import com.ntg.vocabs.vm.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWithPasswordScreen(
    navController: NavController,
    email: String,
    isNew: Boolean,
    loginViewModel: LoginViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                navController = navController,
                email,
                loginViewModel,
                isNew
            )
        }
    )
}


@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    email: String,
    loginViewModel: LoginViewModel,
    isNew: Boolean
) {

    val ctx = LocalContext.current

    val password = remember {
        mutableStateOf("")
    }

    val loading = remember {
        mutableStateOf(false)
    }

    var codeLoading by remember {
        mutableStateOf(false)
    }

    val setError = remember {
        mutableStateOf(false)
    }


    LaunchedEffect(key1 = loading.value, block = {
        if (!loading.value) return@LaunchedEffect
        if (isNew) {
            loginViewModel.signUpEmailPassword(
                email,
                password.value,
                onSuccess = {
                    navController.navigate(Screens.AskBackupScreen.name) {
                        popUpTo(0)
                    }

                },
                onFailure = {
                    ctx.toast(R.string.sth_wrong)
                })
        } else {
            loginViewModel.signIn(email, password.value,
                onSuccess = {
                    loading.value = false
                    navController.navigate(Screens.AskBackupScreen.name) {
                        popUpTo(0)
                    }
                },
                onFailure = {
                    loading.value = false
                    if (it.message.orEmpty().contains("password is invalid"))
                        ctx.toast(R.string.password_not_match)
                    else
                        ctx.toast(R.string.sth_wrong)
                })
        }

    })

//    LaunchedEffect(key1 = loading.value, block = {
//        loginViewModel.verifyUserByPassword(
//            email = email,
//            password = password.value
//        ).observe(owner){
//
//            when(it){
//                is NetworkResult.Error -> {
//                    timber("VERIFY_BY_PASS ::: ERR ${it.message}")
//                }
//                is NetworkResult.Loading -> {
//                    timber("VERIFY_BY_PASS ::: LOADING")
//                }
//                is NetworkResult.Success -> {
//                    timber("VERIFY_BY_PASS ::: ${it.data}")
//                    loading.value = false
//                    when(it.data?.message){
//
//                        TypeOfMessagePass.INVALID_TOKEN.name -> {
//                            ctx.toast(ctx.getString(R.string.download_from_google_play))
//                        }
//
//                        TypeOfMessagePass.INCORRECT_PASSWORD.name -> {
//                            ctx.toast(ctx.getString(R.string.incorrect_pass))
//                            setError.value = true
//                        }
//
//                        TypeOfMessagePass.NEW_USER_NO_NAME.name,
//                        TypeOfMessagePass.USER_VERIFIED_NO_NAME.name-> {
//                            loginViewModel.setUserEmail(email)
//                            navController.navigate(Screens.NameScreen.name+"?email=${email}"){
//                                popUpTo(0)
//                            }
//                        }
//
//                        TypeOfMessagePass.USER_NOT_EXIST.name -> {
//                            navController.navigate(Screens.InsertEmailScreen.name)
//                        }
//
//                        TypeOfMessagePass.USER_SET_NAME.name -> {
//                            loginViewModel.setUsername(it.data.data?.name.orEmpty())
//                            loginViewModel.setUserEmail(email)
//                            navController.navigate(Screens.VocabularyListScreen.name) {
//                                popUpTo(0)
//                            }
//                        }
//
//                    }
//                }
//            }
//
//        }
//    })


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
                "Mot de passe",
                "パスワード",
                "Contraseña",
                "Passwort",
                "Parola d'ordine",
                "كلمة المرور",
            ),
            cursor = "\uD83D\uDD12",
        )


        EditText(
            modifier = Modifier
                .padding(top = 64.dp)
                .fillMaxWidth(), label = stringResource(id = R.string.password), text = password,
            setError = setError,
            supportText = if (isNew) stringResource(
                id = R.string.enter_your_password,
                email
            ) else stringResource(
                id = R.string.enter_password_choice, email
            ),
            isPassword = true
        ) {
            setError.value = false
        }


        CustomButton(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.next),
            type = ButtonType.Primary,
            size = ButtonSize.LG,
            loading = loading.value
        ) {

            val result = notEmptyOrNull(password.value, ctx.getString(R.string.pass_requiered))
                .then {
                    enoughDigitsForPass(
                        password.value,
                        ctx.getString(R.string.a_digit_requier)
                    )
                }
                .then {
                    longEnoughForPass(
                        password.value,
                        ctx.getString(R.string.not_enough_pass_lenght)
                    )
                }


            if (result is Failure) {
                ctx.toast(result.errorMessage)
                setError.value = true
            } else {
                setError.value = false
                loading.value = true
            }

        }


        CustomButton(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.use_verification_code),
            type = ButtonType.Primary,
            style = ButtonStyle.TextOnly,
            size = ButtonSize.LG,
            loading = codeLoading
        ) {
//            navController.popBackStack()
            codeLoading = true

            loginViewModel.loginWithCode(email,
                onSuccess = {

                    loginViewModel.sendLoginCode(email,
                        onSuccess = {
                            ctx.toast(R.string.add_new_word)
                        },
                        onFailure = {
                            codeLoading = false
                            ctx.toast(R.string.sth_wrong)
                        })


                }, onFailure = {
                    codeLoading = false
                    ctx.toast(R.string.sth_wrong)

                })




        }


    }
}
