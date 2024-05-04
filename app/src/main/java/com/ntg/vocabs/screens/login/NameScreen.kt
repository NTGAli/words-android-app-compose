package com.ntg.vocabs.screens.login

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
import com.ntg.vocabs.R
import com.ntg.vocabs.UserDataAndSetting
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.EditText
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.fontMedium24
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.LoginViewModel

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

    var text by remember {
        mutableStateOf("I am no one \uD83E\uDD2D")
    }

    var isExistingUsernameApplied by remember {
        mutableStateOf(false)
    }

    if (userData != null && !isExistingUsernameApplied && userData.name.orEmpty().isNotEmpty()){
        text = "I am ${userData.name} \uD83D\uDE0E"
        isExistingUsernameApplied = true
    }

    var loading by remember {
        mutableStateOf(false)
    }




//    if (loading){
//
//        loginViewModel.updateName(
//            name = text.replace("I am ", "").replace("\uD83D\uDE0E", "").replace("\uD83E\uDD2D", ""),
//            email = email.orEmpty().ifEmpty { userData?.email.orEmpty() }
//        ).observe(owner){
//
//            when(it.data){
//
//                "200" -> {
//                    loginViewModel.setUsername(text.replace("I am ", "").replace("\uD83D\uDE0E", "").replace("\uD83E\uDD2D", ""))
//                    if (email.orEmpty().isNotEmpty()){
//                        navController.navigate(Screens.VocabularyListScreen.name)
//                    }else{
//                        navController.popBackStack()
//                    }
//
//                }
//
//                "400" -> {
//                    ctx.toast(ctx.getString(R.string.sth_wrong))
//                }
//
//                "INVALID_TOKEN" -> {
//                    ctx.toast(ctx.getString(R.string.download_from_google_play))
//                }
//
//
//            }
//
//
//        }
//
//
//    }

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
        ), size = ButtonSize.LG){
            loginViewModel.setUsername(text.replace("I am ", "").replace("\uD83D\uDE0E", "").replace("\uD83E\uDD2D", ""))
            navController.popBackStack()
//            navController.navigate(Screens.VocabularyListScreen.name)


        }
    }

}