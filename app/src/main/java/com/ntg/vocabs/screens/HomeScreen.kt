package com.ntg.vocabs.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.HomeAppbar
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.components.ShapeTileWidget
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.Primary100
import com.ntg.vocabs.ui.theme.Primary200
import com.ntg.vocabs.ui.theme.Primary500
import com.ntg.vocabs.ui.theme.Secondary100
import com.ntg.vocabs.ui.theme.Secondary500
import com.ntg.vocabs.ui.theme.Success100
import com.ntg.vocabs.ui.theme.Success500
import com.ntg.vocabs.ui.theme.Warning100
import com.ntg.vocabs.ui.theme.Warning500
import com.ntg.vocabs.ui.theme.fontBold14
import com.ntg.vocabs.util.WindowInfo
import com.ntg.vocabs.util.formatTime
import com.ntg.vocabs.util.getIconStateRevision
import com.ntg.vocabs.util.getSecBetweenTimestamps
import com.ntg.vocabs.util.getStateRevision
import com.ntg.vocabs.util.orDefault
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.util.rememberWindowInfo
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel,
    backupViewModel: BackupViewModel
) {

    val backupOption =
        loginViewModel.getUserData().asLiveData().observeAsState(null).value?.backupOption.orEmpty()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {

            val userData = loginViewModel.getUserData().asLiveData().observeAsState()

            HomeAppbar(
                title = userData.value?.name,
                profileCallback = {
                    navController.navigate(Screens.ProfileScreen.name)
                },
                searchCallback = {
                    navController.navigate(Screens.SearchScreen.name)
                },
                notificationCallback = {
                    navController.navigate(Screens.MessagesBoxScreen.name)
                },
                voiceSearch = {
                    navController.navigate(Screens.AllWordsScreen.name + "?openSearch=${true}" + "&query=$it")
                },
                backupOnClick = {

                    if (backupOption.isEmpty() || backupOption == "Never" || backupOption == "Only when i tap ‘backup’") {
                        navController.navigate(Screens.AskBackupScreen.name)
                    } else {
                        navController.navigate(Screens.BackupScreen.name)
                    }

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


@OptIn(FlowPreview::class)
@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    navController: NavController
) {

    val listId = wordViewModel.currentList().observeAsState().value?.id
    val wordsList: State<List<Word>?> =
        wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState(initial = null)


    val recentWordCount = remember {
        mutableIntStateOf(0)
    }
    val numberOfAllWords = remember {
        mutableIntStateOf(0)
    }
    recentWordCount.value =
        wordViewModel.recentWords(7, listId.orZero()).observeAsState().value.orZero()
    numberOfAllWords.value =
        wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value.orEmpty().size
    val needToReviewCount = remember {
        mutableIntStateOf(0)
    }
    val windowInfo = rememberWindowInfo()

    var totalTime = 0L


    needToReviewCount.value =
        wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value?.filter {
            getStateRevision(
                it.revisionCount,
                it.lastRevisionTime
            ) == 2 || getStateRevision(it.revisionCount, it.lastRevisionTime) == 3
        }.orEmpty().size

    val timeSpent = wordViewModel.getAllValidTimeSpentBaseListId(listId.orZero())
        .observeAsState().value.orEmpty()
        .toMutableStateList()

    timeSpent.forEach {
        if (it.startUnix != null && it.endUnix != null) {
            totalTime += getSecBetweenTimestamps(
                it.startUnix.orDefault(),
                it.endUnix.orDefault()
            )

        }
    }

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = wordViewModel.scrollPos
    )

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex
        }
            .debounce(500L)
            .collectLatest { index ->
                if (index != 0){
                    wordViewModel.scrollPos = index
                }
            }
    }

    if (wordsList.value != null){
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            state = lazyListState
        ) {

            item {
                Text(
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
                    text = stringResource(R.string.workout_report),
                    style = fontBold14(
                        MaterialTheme.colorScheme.onBackground
                    )
                )

                if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) {
                    PhoneScreenMode(
                        needToReviewCount,
                        totalTime,
                        recentWordCount,
                        numberOfAllWords,
                        navController
                    )
                } else {
                    TabletMode(
                        needToReviewCount,
                        totalTime,
                        recentWordCount,
                        numberOfAllWords,
                        navController
                    )
                }


                Text(
                    modifier = Modifier.padding(top = 28.dp),
                    text = stringResource(R.string.words),
                    style = fontBold14(MaterialTheme.colorScheme.onBackground)
                )

            }


            items(wordsList.value.orEmpty()) { word ->

                val painter = getIconStateRevision(word.revisionCount, word.lastRevisionTime)

                SampleItem(
                    title = word.word.toString(),
                    id = word.id,
                    painter = painter,
                    isBookmarked = word.bookmarked.orFalse()
                ) { _, id, _ ->
                    navController.navigate(Screens.WordDetailScreen.name + "?wordId=$id")
                }
            }

            item {
                if (wordsList.value.orEmpty().isNotEmpty()) {
                    Spacer(modifier = Modifier.padding(vertical = 64.dp))
                }
            }

            item {
                if (wordsList.value != null && wordsList.value?.size == 0) {

                    LottieExample()

//                TypewriterText(
//                    modifier = Modifier
//                        .padding(top = 64.dp)
//                        .fillMaxWidth(),
//                    texts = getRandomWord(ctx),
//                    enableVibrate = false,
//                    style = fontMedium24(MaterialTheme.colorScheme.outline)
//                )
                    CustomButton(
                        modifier = Modifier
//                        .offset(y = -(24).dp)
                            .fillMaxWidth(),
                        text = "add first word for this list",
                        style = ButtonStyle.TextOnly,
                        type = ButtonType.Primary
                    ) {
                        navController.navigate(Screens.AddEditScreen.name)
                    }
                }

            }
        }

    }

}

private fun getRandomWord(ctx: Context): List<String> {
    val inputStream = ctx.resources.openRawResource(R.raw.sample_words)
    val reader = BufferedReader(InputStreamReader(inputStream))
    val wordsList = reader.readLines()
    reader.close()
    val shuffledWords = wordsList.shuffled()
    return shuffledWords.take(200)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenDialog(showDialog: Boolean, onClose: () -> Unit) {

    BottomSheetScaffold(
        sheetContent = {},
//        scaffoldState = scaffoldState,
        sheetPeekHeight = 51.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan)
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Cyan)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {

                }
            }
        }
    }
}

@Composable
fun LottieExample() {

    var isPlaying by remember {
        mutableStateOf(true)
    }
    var speed by remember {
        mutableStateOf(1f)
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.ghost_animation)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false

    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(250.dp)
        )
    }
}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE))
        .build()

    return GoogleSignIn.getClient(context, signInOptions)
}

@Composable
fun PhoneScreenMode(
    needToReviewCount: MutableIntState,
    totalTime: Long,
    recentWordCount: MutableIntState,
    numberOfAllWords: MutableIntState,
    navController: NavController
) {
    val ctx = LocalContext.current
    Column {
        Row {
            ShapeTileWidget(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .padding(end = 4.dp),
                title = "${recentWordCount.value} words",
                subTitle = "last 7d ago",
                painter = painterResource(
                    id = R.drawable.ic_new
                ),
                imageTint = Success500,
                imageBackground = Success100
            ) {
                navController.navigate(Screens.RecentWordScreen.name)

            }

            ShapeTileWidget(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .padding(start = 4.dp),
                title = "${numberOfAllWords.value} words",
                subTitle = "total",
                painter = painterResource(
                    id = R.drawable.icons8_w_1
                ),
                imageTint = Primary500,
                imageBackground = Primary100
            ) {
                navController.navigate(Screens.AllWordsScreen.name)
            }
        }

        Row {
            ShapeTileWidget(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .padding(end = 4.dp),
                title = "${needToReviewCount.value} words",
                subTitle = "need to review",
                painter = painterResource(
                    id = R.drawable.icons8_eye_1
                ),
                imageTint = Warning500,
                imageBackground = Warning100
            ) {
                if (needToReviewCount.value != 0) {
                    navController.navigate(Screens.RevisionScreen.name)
                } else if (numberOfAllWords.value != 0) {
                    ctx.toast(ctx.getString(R.string.no_word_for_review))
                } else {
                    ctx.toast(ctx.getString(R.string.need_to_word_review))
                }
            }

            ShapeTileWidget(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .padding(start = 4.dp),
                title = totalTime.formatTime(),
                subTitle = stringResource(R.string.time_spent),
                painter = painterResource(
                    id = R.drawable.icons8_clock_1_1
                ),
                imageTint = Secondary500,
                imageBackground = Secondary100
            ) {
                navController.navigate(Screens.TimeScreen.name)
            }
        }
    }
}


@Composable
fun TabletMode(
    needToReviewCount: MutableIntState,
    totalTime: Long,
    recentWordCount: MutableIntState,
    numberOfAllWords: MutableIntState,
    navController: NavController
) {
    val ctx = LocalContext.current
    Row {
        ShapeTileWidget(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
                .padding(end = 4.dp),
            title = "${recentWordCount.value} words",
            subTitle = "last 7d ago",
            painter = painterResource(
                id = R.drawable.ic_new
            ),
            imageTint = Success500,
            imageBackground = Success100
        ) {
            navController.navigate(Screens.RecentWordScreen.name)

        }

        ShapeTileWidget(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
                .padding(horizontal = 4.dp),
            title = "${numberOfAllWords.value} words",
            subTitle = "total",
            painter = painterResource(
                id = R.drawable.icons8_w_1
            ),
            imageTint = Primary500,
            imageBackground = Primary100
        ) {
            navController.navigate(Screens.AllWordsScreen.name)
        }

        ShapeTileWidget(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
                .padding(horizontal = 4.dp),
            title = "${needToReviewCount.value} words",
            subTitle = "need to review",
            painter = painterResource(
                id = R.drawable.icons8_eye_1
            ),
            imageTint = Warning500,
            imageBackground = Warning100
        ) {
            if (needToReviewCount.value != 0) {
                navController.navigate(Screens.RevisionScreen.name)
            } else if (numberOfAllWords.value != 0) {
                ctx.toast(ctx.getString(R.string.no_word_for_review))
            } else {
                ctx.toast(ctx.getString(R.string.need_to_word_review))
            }
        }

        ShapeTileWidget(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
                .padding(start = 4.dp),
            title = totalTime.formatTime(),
            subTitle = stringResource(R.string.time_spent),
            painter = painterResource(
                id = R.drawable.icons8_clock_1_1
            ),
            imageTint = Secondary500,
            imageBackground = Secondary100
        ) {
            navController.navigate(Screens.TimeScreen.name)
        }
    }
}
