package com.ntg.vocabs.screens.login

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.ntg.vocabs.R
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.DividerLine
import com.ntg.vocabs.components.EditText
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.model.enums.TypeOfMessagePass
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.fontRegular12
import com.ntg.vocabs.util.GoogleAuthUiClient
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.util.validEmail
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.MainEvent
import com.ntg.vocabs.vm.SignInViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertEmailScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    signInViewModel: SignInViewModel,
    skipBtn: Boolean
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                navController,
                loginViewModel,
                signInViewModel,
                skipBtn
            )
        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    signInViewModel: SignInViewModel,
    skipBtn: Boolean
) {

    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()


    val errorMessage = remember {
        mutableStateOf("")
    }

    val setError = remember {
        mutableStateOf(false)
    }

    val loading = remember {
        mutableStateOf(false)
    }

    var loadingScreen by remember {
        mutableStateOf(false)
    }

    var loadingToSignGoogle by remember {
        mutableStateOf(false)
    }

    val email = remember {
        mutableStateOf("")
    }

    if (setError.value) {
        if (email.value.validEmail()) {
            setError.value = false
            errorMessage.value = ""
        }
    }

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartIntentSenderForResult(),
//        onResult = { result ->
//            if (result.resultCode == RESULT_OK) {
//                coroutineScope.launch {
//                    loadingScreen = true
//                    val signInResult = googleAuthUiClient.signInWithIntent(
//                        intent = result.data ?: return@launch
//                    )
//                    signInViewModel.onSignResult(signInResult)
//                }
//            }
//        }
//    )

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val result = account.getResult(ApiException::class.java)
                val credentials = GoogleAuthProvider.getCredential(result.idToken, null)
                loginViewModel.googleSignIn(credentials)
            } catch (it: ApiException) {
                print(it)
            }
        }

    val googleSignInState = loginViewModel.googleState.value
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = googleSignInState, block = {
        if (googleSignInState.success != null) {
            loadingScreen = true
            val userData = googleAuthUiClient.getSingInUser()

            loginViewModel.setUsername(userData?.username.orEmpty())
            loginViewModel.setUserEmail(userData?.email.orEmpty())
            navController.navigate(Screens.VocabularyListScreen.name) {
                popUpTo(0)
            }

//            loginViewModel.verifyUserByGoogle(
//                email = userData?.email.orEmpty(),
//                username = userData?.username,
//                userId = userData?.userId
//            ).observe(owner) {
//
//                when (it) {
//                    is NetworkResult.Error -> {
//                        context.toast(context.getString(R.string.sth_wrong))
//                        loadingScreen = false
//                    }
//
//                    is NetworkResult.Loading -> {
//
//                    }
//
//                    is NetworkResult.Success -> {
//                        loadingScreen = false
//                        when (it.data?.message) {
//
//                            TypeOfMessagePass.INVALID_TOKEN.name -> {
//                                context.toast(context.getString(R.string.download_from_google_play))
//                            }
//
//                            TypeOfMessagePass.USER_VERIFIED_NO_NAME.name -> {
//                                loginViewModel.setUserEmail(userData?.email.orEmpty())
//                                navController.navigate(Screens.NameScreen.name + "?email=${email}") {
//                                    popUpTo(0)
//                                }
//                            }
//
//                            TypeOfMessagePass.USER_SET_NAME.name -> {
//                                loginViewModel.setUsername(it.data.data?.name.orEmpty())
//                                loginViewModel.setUserEmail(userData?.email.orEmpty())
//                                navController.navigate(Screens.VocabularyListScreen.name) {
//                                    popUpTo(0)
//                                }
//                            }
//
//                        }
//                    }
//                }
//
//            }
        }
    })


//    if (signWithGoogle.value) {
//        coroutineScope.launch {
//            val signInIntentSender = googleAuthUiClient.signIn()
//            launcher.launch(
//                IntentSenderRequest.Builder(
//                    signInIntentSender ?: return@launch
//                ).build()
//            )
//        }
//        signWithGoogle.value = false
//    }


    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            TypewriterText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 64.dp),
                texts = listOf(
                    "Hello",
                    "Bonjour",
                    "こんにちは",
                    "Hola",
                    "Hallo",
                    "Ciao",
                    "مرحبًا",
                ),
                cursor = "\uD83D\uDC4B",
            )


            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp),
                text = stringResource(R.string.continiue_with_google), iconStart = painterResource(
                    id = R.drawable.google_logo
                ), loading = loadingToSignGoogle, type = ButtonType.Variance, size = ButtonSize.LG
            ) {
                loadingToSignGoogle = true
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken("894409702853-5mksiplcri4k17rtdpc119u790noq1e2.apps.googleusercontent.com")
                    .build()

                val googleSingInClient = GoogleSignIn.getClient(context, gso)

                launcher.launch(googleSingInClient.signInIntent)

            }


            DividerLine(
                modifier = Modifier.padding(top = 24.dp),
                title = stringResource(id = R.string.or)
            )



            EditText(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(), label = stringResource(id = R.string.email), text = email,
                setError = setError, supportText = errorMessage.value
            )

            CustomButton(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                loading = loading.value,
                text = stringResource(id = R.string.next),
                type = ButtonType.Primary,
                size = ButtonSize.LG
            ) {
                if (email.value.validEmail()) {

                    loginViewModel.loginWithEmail(email.value).observe(owner) {

                        when (it) {
                            is NetworkResult.Error -> {
                                timber("VerifyUser::: ERR ::: ${it.message}")
                            }

                            is NetworkResult.Loading -> {
                                timber("VerifyUser:::")
                                loading.value = true
                            }

                            is NetworkResult.Success -> {
                                timber("VerifyUser::: S :: ${it.data}")
                                if (it.data?.isSuccess.orFalse()) {
                                    timber("VerifyUser:::")
                                    timber("VerifyUser 1111111111")
                                    navController.navigate(Screens.LoginWithPasswordScreen.name + "?email=${email.value}&isNew=${it.data?.message.orEmpty() == "NEW_USER"}")
                                } else if (it.data?.message == "INVALID_TOKEN") {
                                    timber("VerifyUser 222222222")
                                    context.toast(context.getString(R.string.download_from_google_play))
                                } else {
                                    timber("VerifyUser 3333333")
                                    context.toast(context.getString(R.string.sth_wrong))
                                }
                                timber("VerifyUser 444444444444")
                            }
                        }
                    }
                } else {
                    errorMessage.value = context.getString(R.string.invalid_email)
                    setError.value = true
                }
            }

            if (skipBtn) {
                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = ButtonStyle.TextOnly,
                    text = stringResource(id = R.string.skip),
                    type = ButtonType.Primary,
                    size = ButtonSize.LG
                ) {
                    loginViewModel.setSkipLogin(true)
                    navController.navigate(Screens.VocabularyListScreen.name)
                }
            }


            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(id = R.string.agree_policy),
                style = fontRegular12(MaterialTheme.colorScheme.onBackground)
            )
        }

        if (loadingScreen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

    }


}

