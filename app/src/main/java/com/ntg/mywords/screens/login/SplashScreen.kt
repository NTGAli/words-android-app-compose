package com.ntg.mywords.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.util.orZero
import com.ntg.mywords.util.timber
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.WordViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    wordViewModel: WordViewModel
) {
    val userEmail = loginViewModel.getUserData().asLiveData().observeAsState()
    val lists = wordViewModel.getAllVocabList().observeAsState()


    timber("VOCAB_LISTS ::::::: $lists")
    timber("USER_EMAIL :::::::: ${userEmail.value?.email}")

    LaunchedEffect(userEmail) {
        delay(200)
        if (userEmail.value?.email.orEmpty().isEmpty()) {
            navController.navigate(Screens.CodeScreen.name) {
                popUpTo(0)
            }
        } else if (lists.value?.size.orZero() == 0 || lists.value?.filter { it.isSelected }.orEmpty()
                .isEmpty()
        ) {
            navController.navigate(Screens.VocabularyListScreen.name) {
                popUpTo(0)
            }
        }else{
            navController.navigate(Screens.HomeScreen.name) {
                popUpTo(0)
            }
        }
    }


}