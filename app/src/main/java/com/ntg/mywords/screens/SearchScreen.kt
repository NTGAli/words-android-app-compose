package com.ntg.mywords.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.mywords.R
import com.ntg.mywords.components.EditText
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    navHostController: NavHostController
) {

    val tabData = listOf(
        stringResource(id = R.string.your_words),
        stringResource(id = R.string.all_words),
    )

    val pagerState = rememberPagerState { 2 }

    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        topBar = {

            Column {
//                Appbar(
//                    title = stringResource(id = R.string.challenge_monthly),
//                    enableNavigation = true,
//                    navigationOnClick = {
//                        navHostController.popBackStack()
//                    })


                val word = remember {
                    mutableStateOf("")
                }
                EditText(
                    Modifier
                        .fillMaxWidth(), text = word, label = stringResource(R.string.word),
                    leadingIcon = ImageVector.vectorResource(id = R.drawable.microphone_02),
                    enabledLeadingIcon = true,
                    leadingIconOnClick = {
//                        openVoiceToSpeech = true
//                        voiceForWord = true
                    }
                )

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
                    if (tabData[index] == stringResource(id = R.string.your_words)) {
//                        ParticipatesScreen(navController = navHostController, stepViewModel, uid)
                    } else {
//                        WinnersScreen(navController = navHostController, stepViewModel)
                    }
                }
            }
        }
    )

}