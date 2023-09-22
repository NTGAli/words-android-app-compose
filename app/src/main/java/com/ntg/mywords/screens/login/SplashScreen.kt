package com.ntg.mywords.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.mywords.components.LoadingView
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

    LoadingView()



//    val userData = loginViewModel.getUserData().asLiveData().observeAsState()
//    val lists = wordViewModel.getAllVocabList().observeAsState()
//
//
//    timber("VOCAB_LISTS ::::::: $lists")
//    timber("USER_EMAIL :::::::: ${userData.value?.email}")
//    timber("USER_NAME :::::::: ${userData.value?.name}")
//
//    LaunchedEffect(userData) {
//        delay(900)
//        if (userData.value?.email.orEmpty().isEmpty()) {
//            if (userData.value?.isSkipped.orFalse() && (lists.value?.size.orZero() != 0)) {
//
//                if (lists.value?.filter { it.isSelected }
//                        .orEmpty()
//                        .isNotEmpty()){
//                    navController.navigate(Screens.HomeScreen.name)
//                }else{
//                    navController.navigate(Screens.VocabularyListScreen.name)
//                }
//            }else{
//                navController.navigate(Screens.InsertEmailScreen.name)
//            }
//        } else if (lists.value?.size.orZero() == 0 || lists.value?.filter { it.isSelected }
//                .orEmpty()
//                .isEmpty()
//        ) {
//            navController.navigate(Screens.VocabularyListScreen.name)
//        } else {
//            navController.navigate(Screens.HomeScreen.name)
//        }
//    }


}