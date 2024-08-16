package com.ntg.vocabs.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.EditText
import com.ntg.vocabs.components.OpenVoiceSearch
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    navHostController: NavHostController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel
) {

    var openVoiceToSpeech by rememberSaveable {
        mutableStateOf(false)
    }

    var voiceForWord by rememberSaveable {
        mutableStateOf(false)
    }

    val language = wordViewModel.currentList().observeAsState().value?.language.orEmpty()

    val tabData = listOf(
        stringResource(id = R.string.your_words),
        if (language == "English") stringResource(id = R.string.all_words) else stringResource(id = R.string.verbs),
    )

    val pagerState = rememberPagerState {
        if (language == "English" || language == "German") tabData.size else 1
    }

    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    val query = rememberSaveable {
        mutableStateOf("")
    }

    OpenVoiceSearch(openVoiceToSpeech) {
        if (it != null){
            query.value = it
        }
        openVoiceToSpeech = false
    }
    Scaffold(
        topBar = {
            Column {
                EditText(
                    Modifier
                        .fillMaxWidth(),
                    text = query, label = stringResource(R.string.word),
                    leadingIcon = ImageVector.vectorResource(id = R.drawable.microphone_02),
                    enabledLeadingIcon = true,
                    searchMode = true,
                    leadingIconOnClick = {
                        openVoiceToSpeech = true
                        voiceForWord = true
                    },
                    leftIconOnClick = {
                        navHostController.popBackStack()
                    }
                )

                if (language == "English" || language == "German") {
                    TabRow(
                        selectedTabIndex = tabIndex,
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.background,
                        divider = {
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
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
        },
        content = { paddingValues ->
            // PAGER
            HorizontalPager(
                modifier = Modifier.padding(paddingValues),
                state = pagerState
            ) { index ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (language == "English" || language == "German") {
                        if (tabData[index] == stringResource(id = R.string.your_words)) {
                            YourWordScreen(
                                navController = navHostController,
                                wordViewModel = wordViewModel,
                                loginViewModel,
                                query = query.value
                            )
                        } else if (tabData[index] == stringResource(id = R.string.all_words)) {
                            AllWordScreen(
                                navController = navHostController,
                                wordViewModel = wordViewModel,
                                q = query.value
                            )
                        } else if (tabData[index] == stringResource(id = R.string.verbs)) {
                            AllGermanVerbsScreen(
                                navController = navHostController,
                                wordViewModel = wordViewModel,
                                query = query.value
                            )
                        }
                    } else {
                        YourWordScreen(
                            navController = navHostController,
                            wordViewModel = wordViewModel,
                            loginViewModel,
                            query = query.value
                        )
                    }
                }
            }
        }
    )

}