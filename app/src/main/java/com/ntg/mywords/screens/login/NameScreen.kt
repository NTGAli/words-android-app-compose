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
import com.ntg.mywords.UserDataAndSetting
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.EditText
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.fontMedium24
import com.ntg.mywords.util.Constant
import com.ntg.mywords.util.timber
import com.ntg.mywords.util.toast
import com.ntg.mywords.vm.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameScreen(navController: NavController, loginViewModel: LoginViewModel, email: String? = null){

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            val userData = loginViewModel.getUserData().asLiveData().observeAsState()
            Content(paddingValues = innerPadding, navController, loginViewModel, email, userData.value)
        }
    )

}

@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController, loginViewModel: LoginViewModel, email: String? = null, userData: UserDataAndSetting?){

    val owner = LocalLifecycleOwner.current
    val ctx = LocalContext.current


    var text by remember {
        mutableStateOf("I am no one \uD83E\uDD2D")
    }

    var isExistingUsernameApplied by remember {
        mutableStateOf(false)
    }

    if (userData != null && !isExistingUsernameApplied){
        text = "I am ${userData.name} \uD83D\uDE0E"
        isExistingUsernameApplied = true
    }

    var loading by remember {
        mutableStateOf(false)
    }




    if (loading){

        loginViewModel.updateName(
            name = text.replace("I am ", "").replace("\uD83D\uDE0E", "").replace("\uD83E\uDD2D", ""),
            email = email ?: userData?.email.orEmpty()
        ).observe(owner){

            when(it.data){

                "200" -> {
                    loginViewModel.setUsername(text.replace("I am ", "").replace("\uD83D\uDE0E", "").replace("\uD83E\uDD2D", ""))
                    if (email.orEmpty().isNotEmpty()){
                        navController.navigate(Screens.VocabularyListScreen.name)
                    }else{
                        navController.popBackStack()
                    }

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
        Text(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(vertical = 64.dp), text = text, style = fontMedium24(MaterialTheme.colorScheme.onBackground))


        EditText(label = stringResource(id = R.string.name), onChange = {
            if (it != ""){
                text = "I am $it \uD83D\uDE0E"
            }else{
                text = "I am no one \uD83E\uDD2D"
            }
        })

        CustomButton(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), text = if (text.contains("no one")) stringResource(id = R.string.prefer_not_to_say) else stringResource(
            id = R.string.next
        ), size = ButtonSize.LG, loading = loading){

            if (text.contains("no one")){
                navController.navigate(Screens.VocabularyListScreen.name)
            }else{
                loading = true
            }

        }
    }

}