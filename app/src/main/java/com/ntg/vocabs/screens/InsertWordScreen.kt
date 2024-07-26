package com.ntg.vocabs.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.ui.theme.Secondary500
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InsertWordScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel
){
    val language = wordViewModel.currentList().observeAsState().value?.language.orEmpty()

    var tabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val pagerState = rememberPagerState(pageCount = {
        if (language == "English") 2 else 1
    }, initialPage = wordViewModel.currentPage)

//    LaunchedEffect(key1 = pagerState) {
//        tabIndex = pagerState.currentPage
//    }

    val coroutineScope = rememberCoroutineScope()

    val tabData = listOf(
        stringResource(id = R.string.manually),
        stringResource(id = R.string.dictionary),
    )



    Scaffold(
        topBar = {
            Column {
                Row(modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Rounded.KeyboardArrowLeft, contentDescription = null, tint = Secondary500)
                    }

                    Text(text = stringResource(id = R.string.add_new_word), style = fontMedium12())
                }

                if (language == "English"){
                    TabRow(
                        selectedTabIndex = tabIndex,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        tabData.forEachIndexed { index, title ->
                            Tab(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                text = { Text(title) },
                                selected = tabIndex == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                selectedContentColor = MaterialTheme.colorScheme.primary,
                                unselectedContentColor = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                }
            }

        }
    ) {
        HorizontalPager(
            modifier = Modifier.padding(it),
            state = pagerState) { page ->

            LaunchedEffect(key1 = page) {
                wordViewModel.currentPage = page
            }

            if (page == 0) {
                AddEditWordScreen(navController = navController, wordViewModel = wordViewModel, loginViewModel = loginViewModel, showAppbar = false, wordId = -1)
            }else{
                AllWordScreen(
                    navController = navController,
                    wordViewModel = wordViewModel,
                    q = "",
                    showSearchbar = true
                )
            }

        }
    }



}