package com.ntg.mywords.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.ntg.mywords.R
import com.ntg.mywords.UserDataAndSetting
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.SampleItem
import com.ntg.mywords.components.ShapeTileWidget
import com.ntg.mywords.model.components.AppbarItem
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.model.req.BackupUserData
import com.ntg.mywords.model.response.WordData
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.*
import com.ntg.mywords.util.*
import com.ntg.mywords.vm.WordViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, wordViewModel: WordViewModel) {

    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current

//    wordViewModel.setUserEmail("aliNtg@outlook.com")


    timber("akljeflkejflkejfelk :::::::: ${
        wordViewModel.getUserData().asLiveData().observeAsState().value?.email
    }")


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.my_words),
                enableNavigation = false,
                scrollBehavior = scrollBehavior,
                actions = listOf(
                    AppbarItem(
                        id = 0,
                        imageVector = ImageVector.vectorResource(id = R.drawable.settings)
                    )
                ),
                actionOnClick = {
                    navController.navigate(Screens.SettingScreen.name)



//                    wordViewModel.getMyWords().observe(owner){words ->
//                        wordViewModel.getAllValidTimeSpent().observe(owner){times ->
//
//
//                            val wordData = BackupUserData(words = words, totalTimeSpent = times)
//
//
//
//
//                            wordViewModel.upload(wordData, "alintg14@gmail.com").observe(owner) {
//
//                                when (it) {
//                                    is NetworkResult.Error -> {
//                                        timber("ajhwfjkwahfjhwakjfhawkjhf :::: ERR ${it.message}")
//                                    }
//                                    is NetworkResult.Loading -> {
//                                        timber("ajhwfjkwahfjhwakjfhawkjhf :::: LD")
//                                    }
//                                    is NetworkResult.Success -> {
//                                        timber("ajhwfjkwahfjhwakjfhawkjhf :::: ${it.data}")
//                                    }
//                                }
//
//                            }
//
//
//                        }
//                    }


                }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, wordViewModel, navController)

        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.AddEditScreen.name)
                },
                containerColor = Primary200
            ) {
                Icon(imageVector = Icons.Rounded.Add, tint = Color.Black, contentDescription = "FL")
            }
        }
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    navController: NavController
) {

    val modalBottomSheetState =
        rememberBottomSheetScaffoldState(SheetState(skipPartiallyExpanded = false))
    val scope = rememberCoroutineScope()

//    BoxWithConstraints() {
//        val maxHeight = maxHeight
//        val sheetHeight = maxHeight / 4.dp
//        BottomSheetScaffold(
//            modifier = Modifier,
//            scaffoldState = modalBottomSheetState,
//            sheetContent = {
//
//            },
//            containerColor = Color.White,
//            sheetPeekHeight = 51.dp
//        ) {
//            Box(modifier = Modifier
//                .height(900.dp)) {
//
//                Column(Modifier.background(MaterialTheme.colorScheme.primary).fillMaxSize()) {
//
//                }
//            }
//        }
//    }

    val wordsList: State<List<Word>?> = wordViewModel.getMyWords().observeAsState()

    LazyColumn(modifier = Modifier.padding(paddingValues)) {

        item {

            val recentWordCount = remember {
                mutableStateOf(0)
            }
            val needToReviewCount = remember {
                mutableStateOf(0)
            }
            val numberOfAllWords = remember {
                mutableStateOf(0)
            }

            var totalTime = 0L



            recentWordCount.value = wordViewModel.recentWords(7).observeAsState().value.orZero()
            needToReviewCount.value = wordViewModel.getMyWords().observeAsState().value?.filter {
                getStateRevision(
                    it.revisionCount,
                    it.lastRevisionTime
                ) == 2 || getStateRevision(it.revisionCount, it.lastRevisionTime) == 3
            }.orEmpty().size
            numberOfAllWords.value =
                wordViewModel.getMyWords().observeAsState().value.orEmpty().size
            val timeSpent = wordViewModel.getAllValidTimeSpent().observeAsState().value.orEmpty()
                .toMutableStateList()

            timeSpent.forEach {
                if (it.startUnix != null && it.endUnix != null) {
                    totalTime += getSecBetweenTimestamps(
                        it.startUnix.orDefault(),
                        it.endUnix.orDefault()
                    )
                }
            }

//            wordViewModel.getAllValidTimeSpent().observeAsState().value?.forEach {
//                if (it.startUnix != null && it.endUnix != null) {
//                    timeSpent.value += getSecBetweenTimestamps(it.startUnix, it.endUnix!!)
//                    timber("TTTTTTTTTTTTTTTTTTTTTTTTTTTTT :: ${timeSpent.value} -- ${getSecBetweenTimestamps(it.startUnix.orDefault(), it.endUnix.orDefault())}")
//                }
//            }
            timber("kalwjdklwjadkjwaldkjw ${7.getUnixTimeNDaysAgo()} --- ${System.currentTimeMillis()} --- ${recentWordCount.value}")

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp),
                text = stringResource(R.string.workout_report),
                style = fontBold14(
                    MaterialTheme.colorScheme.onBackground
                )
            )

            Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                ShapeTileWidget(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    title = "${recentWordCount.value} words",
                    subTitle = "last 7d ago",
                    painter = painterResource(
                        id = R.drawable.ic_new
                    ),
                    imageTint = Success500
                ) {
                    navController.navigate(Screens.RecentWordScreen.name)

                }

                ShapeTileWidget(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    title = "${numberOfAllWords.value} words",
                    subTitle = "total",
                    painter = painterResource(
                        id = R.drawable.icons8_w_1
                    ),
                    imageTint = Primary500
                ) {
                    navController.navigate(Screens.AllWordsScreen.name)
                }
            }

            Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                ShapeTileWidget(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    title = "${needToReviewCount.value} words",
                    subTitle = "need to review",
                    painter = painterResource(
                        id = R.drawable.icons8_eye_1
                    ),
                    imageTint = Warning500
                ) {
                    navController.navigate(Screens.RevisionScreen.name)
                }

                ShapeTileWidget(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    title = totalTime.formatTime(),
                    subTitle = stringResource(R.string.time_spent),
                    painter = painterResource(
                        id = R.drawable.icons8_clock_1_1
                    ),
                    imageTint = Secondary500
                ) {
                    navController.navigate(Screens.TimeScreen.name)
                }
            }

            Text(
                modifier = Modifier.padding(top = 28.dp, start = 16.dp),
                text = stringResource(R.string.words),
                style = fontBold14(MaterialTheme.colorScheme.onBackground)
            )

        }


        items(wordsList.value.orEmpty()) { word ->

            val painter = getIconStateRevision(word.revisionCount, word.lastRevisionTime)

            SampleItem(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = word.word.toString(),
                id = word.id,
                painter = painter
            ) { _, id, _ ->
                navController.navigate(Screens.WordDetailScreen.name + "?wordId=$id")
            }
        }


    }


}


