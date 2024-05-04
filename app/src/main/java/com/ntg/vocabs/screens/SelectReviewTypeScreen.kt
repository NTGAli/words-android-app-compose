package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.DescriptionType
import com.ntg.vocabs.components.NeedProDialog
import com.ntg.vocabs.components.ReviewItem
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.util.getStateRevision
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectReviewTypeScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel
){

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.revision),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding,navController, wordViewModel, loginViewModel)

        }
    )
}


@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel
){

    val isPurchased = loginViewModel.getUserData().collectAsState(initial = null).value?.isPurchased.orFalse()
    val email = loginViewModel.getUserData().collectAsState(initial = null).value?.email

    val ctx = LocalContext.current

    var needToReviewCount by remember {
        mutableIntStateOf(0)
    }

    var openDialog by remember {
        mutableStateOf(false)
    }

    val listId = wordViewModel.currentList().observeAsState().value?.id
    val wordsCount =
        wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value.orEmpty().size

    needToReviewCount =
        wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value?.filter {
            getStateRevision(
                it.revisionCount,
                it.lastRevisionTime
            ) == 2 || getStateRevision(it.revisionCount, it.lastRevisionTime) == 3
        }.orEmpty().size

    LazyColumn(
        modifier = Modifier.padding(paddingValues)
    ){

        item {
            ReviewItem(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                count = needToReviewCount, title = stringResource(id = R.string.review), isPro = false) {
                if (needToReviewCount != 0){
                    navController.navigate(Screens.RevisionScreen.name)
                }else{
                    ctx.toast(ctx.getString(R.string.no_word_for_review))
                }
            }
        }


        item {
            ReviewItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
                count = -1, title = stringResource(id = R.string.random_review), isPro = !isPurchased) {
                if (isPurchased){
                    if (wordsCount > 2){
                        navController.navigate(Screens.RevisionScreen.name + "?isRandom=${true}")
                    }else{
                        ctx.toast(ctx.getString(R.string.add_more_than_two_words))
                    }
                }else{
                    openDialog = true
                }
            }
        }


        item {
            ReviewItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
                count = -1, title = stringResource(id = R.string.writing), isPro = !isPurchased) {

                if (isPurchased){
                    if (wordsCount > 2){
                        navController.navigate(Screens.WritingScreen.name)
                    }else{
                        ctx.toast(ctx.getString(R.string.add_more_than_two_words))
                    }
                }else{
                    openDialog = true
                }
            }
        }


    }

    if (openDialog){
        NeedProDialog(
            type = DescriptionType.LIST,
            onClick = {
                if (email.orEmpty().isNotEmpty()){
                    navController.navigate(Screens.PaywallScreen.name)
                }else{
                    navController.navigate(Screens.GoogleLoginScreen.name + "?skip=${false}")
                }
        }) {
            openDialog = false
        }
    }


}