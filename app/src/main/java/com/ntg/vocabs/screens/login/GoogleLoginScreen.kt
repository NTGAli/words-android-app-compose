package com.ntg.vocabs.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.util.GoogleAuthUiClient
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.util.validEmail
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.SignInViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleLoginScreen(navController: NavHostController,
                      loginViewModel: LoginViewModel,
                      signInViewModel: SignInViewModel,
                      backupViewModel: BackupViewModel,
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
                backupViewModel,
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
    backupViewModel: BackupViewModel,
    skipBtn: Boolean
) {

    val context = LocalContext.current


    val errorMessage = remember {
        mutableStateOf("")
    }

    var typeCount by remember {
        mutableIntStateOf(1)
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

    val email = rememberSaveable {
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

    LaunchedEffect(key1 = googleSignInState, block = {
        if (googleSignInState.success != null) {
            loadingScreen = true
            val userData = googleAuthUiClient.getSingInUser()



            backupViewModel.restoreBackupFromServer(context, userData?.email.orEmpty()){
                context.toast(R.string.sth_wrong)
                loading.value = false
            }

            backupViewModel.restoreVocabularies(userData?.email.orEmpty()){
                loginViewModel.checkBackup(it)
                loginViewModel.setUsername(userData?.username.orEmpty())
                loginViewModel.setUserEmail(userData?.email.orEmpty())
                loading.value = false
                if (!skipBtn){
                    navController.navigate(Screens.PaywallScreen.name)
                }
            }

        } else if (googleSignInState.error != null) {
            loadingToSignGoogle = false
        }
    })


    Column(
        modifier = Modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (skipBtn){
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {

                if (typeCount >= 1){
                    TypewriterText(
                        texts = listOf(
                            "Create Lists \uD83D\uDCDD",
                        ),
                        singleText = true
                    ){
                        if (it){
                            typeCount = 2
                        }
                    }
                }

                if (typeCount >= 2){
                    TypewriterText(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        texts = listOf(
                            "Add Words ➕",
                        ),
                        singleText = true
                    ){
                        if (it){
                            typeCount = 3
                        }
                    }
                }

                if (typeCount >= 3){
                    TypewriterText(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        texts = listOf(
                            "Review Easily \uD83D\uDCDA",
                        ),
                        singleText = true
                    )
                }
            }
        }else{
            TypewriterText(
                modifier = Modifier
//                    .padding(top = 64.dp)
                    .weight(1f),
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
        }


        CustomButton(
            modifier = Modifier
                .fillMaxWidth(),
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

        if (skipBtn) {
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                style = ButtonStyle.TextOnly,
                text = stringResource(id = R.string.skip),
                type = ButtonType.Primary,
                size = ButtonSize.LG
            ) {
                loginViewModel.setSkipLogin(true)
            }
        }

        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.agree_policy),
            style = fontMedium14(MaterialTheme.colorScheme.outline)
        )

        Row(
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        ) {
            CustomButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                text = stringResource(id = R.string.privacy_policy), style = ButtonStyle.TextOnly, size = ButtonSize.LG){
                navController.navigate(Screens.PrivacyPolicyScreen.name)
            }
            CustomButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                text = stringResource(id = R.string.terms_conditions), style = ButtonStyle.TextOnly, size = ButtonSize.LG){
                navController.navigate(Screens.TermsAndConditionsScreen.name)
            }
        }

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

