package com.ntg.vocabs.screens.login

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ntg.vocabs.components.LoadingView
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel

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