package com.ntg.mywords.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.util.orFalse
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
    val userData = loginViewModel.getUserData().asLiveData().observeAsState()
    val lists = wordViewModel.getAllVocabList().observeAsState()


    timber("VOCAB_LISTS ::::::: $lists")
    timber("USER_EMAIL :::::::: ${userData.value?.email}")

    LaunchedEffect(userData) {
        delay(200)
        if (userData.value?.email.orEmpty().isEmpty()) {
            if (userData.value?.isSkipped.orFalse() && (lists.value?.size.orZero() != 0 || lists.value?.filter { it.isSelected }
                    .orEmpty()
                    .isNotEmpty())) {
                navController.navigate(Screens.HomeScreen.name) {
                    popUpTo(0)
                }
            }else{
                navController.navigate(Screens.InsertEmailScreen.name) {
                    popUpTo(0)
                }
            }
        } else if (lists.value?.size.orZero() == 0 || lists.value?.filter { it.isSelected }
                .orEmpty()
                .isEmpty()
        ) {
            navController.navigate(Screens.VocabularyListScreen.name) {
                popUpTo(0)
            }
        } else {
            navController.navigate(Screens.HomeScreen.name) {
                popUpTo(0)
            }
        }
    }


}