package com.ntg.mywords.screens.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.R
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.EditText
import com.ntg.mywords.components.TypewriterText
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.fontRegular12
import com.ntg.mywords.util.timber
import com.ntg.mywords.util.toast
import com.ntg.mywords.util.validEmail
import com.ntg.mywords.vm.LoginViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertEmailScreen(navController: NavHostController, loginViewModel: LoginViewModel) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navController, loginViewModel)
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, navController: NavHostController, loginViewModel: LoginViewModel) {

    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current

    val errorMessage  = remember {
        mutableStateOf("")
    }

    val setError = remember {
        mutableStateOf(false)
    }

    val loading = remember {
        mutableStateOf(false)
    }

    val email = remember {
        mutableStateOf("")
    }

    if (setError.value){
        if (email.value.validEmail()){
            setError.value = false
            errorMessage.value = ""
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp),

        ) {

        TypewriterText(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 64.dp),
            texts = listOf(
                "Hello",
                "سلام"
            ),
            cursor = "\uD83D\uDC4B",
        )


        EditText(modifier = Modifier
            .padding(top = 64.dp)
            .fillMaxWidth(), label = stringResource(id = R.string.email), text = email,
        setError = setError, supportText = errorMessage.value)


        Text(modifier = Modifier.padding(top = 16.dp), text = stringResource(id = R.string.agree_policy), style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))
        CustomButton(modifier = Modifier.padding(top = 8.dp), loading = loading.value, text = stringResource(id = R.string.next), type = ButtonType.Primary, size = ButtonSize.LG){

            if (email.value.validEmail()){

                loginViewModel.sendCode(email.value).observe(owner){

                    when(it){
                        is NetworkResult.Error -> {
                            timber("VerifyUser::: ERR ::: ${it.message}")
                        }
                        is NetworkResult.Loading -> {
                            timber("VerifyUser:::")
                            loading.value = true
                        }
                        is NetworkResult.Success -> {
                            timber("VerifyUser:::")
                            loading.value = false

                            if (it.data == "200"){
                                navController.navigate(Screens.CodeScreen.name+"?email=${email.value}")
                            }else{
                                context.toast(context.getString(R.string.sth_wrong))
                            }

                        }
                    }

                }


            }else{
                errorMessage.value = context.getString(R.string.invalid_email)
                setError.value = true
            }


        }



    }

}

