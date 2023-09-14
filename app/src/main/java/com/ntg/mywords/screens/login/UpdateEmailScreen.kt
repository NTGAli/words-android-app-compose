package com.ntg.mywords.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.EditText
import com.ntg.mywords.components.TypewriterText
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.util.toast
import com.ntg.mywords.util.validEmail
import com.ntg.mywords.vm.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateEmailScreen(navController: NavController, loginViewModel: LoginViewModel){

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            val userData = loginViewModel.getUserData().asLiveData().observeAsState()
            Content(paddingValues = innerPadding, navController, loginViewModel,userData.value?.email)
        }
    )

}


@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController, loginViewModel: LoginViewModel,email: String?){


    val owner = LocalLifecycleOwner.current
    val ctx = LocalContext.current


    val currentEmail = remember {
        mutableStateOf("")
    }

    val newEmail = remember {
        mutableStateOf("")
    }

    var isExistingUsernameApplied by remember {
        mutableStateOf(false)
    }


    var loading by remember {
        mutableStateOf(false)
    }




    if (loading){

        loginViewModel.updateEmail(
            email = email.orEmpty(),
            newEmail = newEmail.value
        ).observe(owner){

            when(it.data){

                "200" -> {
                    loginViewModel.setUserEmail(newEmail.value)
                    navController.popBackStack()
                }

                "400" -> {
                    ctx.toast(ctx.getString(R.string.sth_wrong))
                }

                "INVALID_TOKEN" -> {
                    ctx.toast(ctx.getString(R.string.download_from_google_play))
                }


            }


        }


    }

    Column(modifier = Modifier.padding(horizontal = 32.dp)) {
        TypewriterText(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 64.dp, bottom = 32.dp),
            texts =
                listOf(
                    "Email",
                    "Eメール",
                    "Correo electrónico",
                    "بريد إلكتروني"
                ),
            cursor = "\uD83D\uDC4B",
        )


        EditText(label = stringResource(id = R.string.current_email), text = currentEmail)


        EditText(label = stringResource(id = R.string.new_mail), text = newEmail)

        CustomButton(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), text =stringResource(
            id = R.string.change
        ), size = ButtonSize.LG, loading = loading){

           if (currentEmail.value == email){
               if (newEmail.value.validEmail()){
                   loading = true
               }else{
                   ctx.toast(ctx.getString(R.string.invalid_email))
               }
           }else{
               ctx.toast(ctx.getString(R.string.invalid_current_email))
           }

        }
    }



}