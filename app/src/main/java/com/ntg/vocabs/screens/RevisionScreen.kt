package com.ntg.vocabs.screens

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.SelectableImage
import com.ntg.vocabs.components.SimpleReviewItem
import com.ntg.vocabs.model.ReviewTypes
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.playback.AndroidAudioPlayer
import com.ntg.vocabs.ui.theme.*
import com.ntg.vocabs.util.getStateRevision
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.toPronunciation
import com.ntg.vocabs.vm.CalendarViewModel
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    calendarViewModel: CalendarViewModel
) {

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

            Content(paddingValues = innerPadding, wordViewModel, navController)

        }
    )

//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        HandleLifecycle(calendarViewModel, wordViewModel)
//    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    navController: NavController,
) {

    val reviewTypes: ArrayList<ReviewTypes> = arrayListOf(
//        ReviewTypes.Simple,
//        Pair(ReviewTypes.Word, 0),
//        Pair(ReviewTypes.Definition, 0),
//        Pair(ReviewTypes.Sound, 0),
//        Pair(ReviewTypes.Example, 0),
//        Pair(ReviewTypes.Image, 0)
    )

    val ctx = LocalContext.current
    val player by lazy {
        AndroidAudioPlayer(ctx)
    }
    val scope = rememberCoroutineScope()

    var isCorrect by remember {
        mutableStateOf<Boolean?>(null)
    }

    var correctAnswer by remember {
        mutableStateOf("")
    }

    var wordSelected by remember {
        mutableStateOf("")
    }

    var shuffledList by remember {
        mutableStateOf<List<Word>>(listOf())
    }

    var showSheet by remember { mutableStateOf(false) }


    val listId = wordViewModel.currentList().observeAsState().value?.id
    val allWords =
        wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value.orEmpty()
    var words =
        allWords.filter {
            getStateRevision(
                it.revisionCount,
                it.lastRevisionTime
            ) == 2 || getStateRevision(
                it.revisionCount,
                it.lastRevisionTime
            ) == 3
        }

    val rejectedList = remember {
        mutableStateListOf<Word>()
    }


    words = words.filterNot { it in rejectedList }

    if (words.isNotEmpty()) {

        val word = words.get(0)


        reviewTypes.clear()


        if (showSheet) {
            BottomSheet(
                isCorrect.orFalse(),
                correctAnswer
            ) {
                if (wordSelected == word.word.orEmpty()) {
                    word.revisionCount = word.revisionCount + 1
                    word.lastRevisionTime = System.currentTimeMillis()
                    wordViewModel.editWord(word.id, word)
                    rejectedList.add(word)
                } else {
                    rejectedList.add(word)
                }
                showSheet = false
                isCorrect = null
            }
        }

        if (word.definition.orEmpty().isNotEmpty() && allWords.filter {
                it.definition.orEmpty().isNotEmpty()
            }.size > 4) reviewTypes.add(ReviewTypes.Definition)
        if ((word.sound.orEmpty().isNotEmpty() || word.voice.orEmpty()
                .isNotEmpty()) && allWords.size > 4
        ) reviewTypes.add(ReviewTypes.Sound)
        if (word.example.orEmpty().any { it.contains(word.word.orEmpty()) } && allWords.size > 4) reviewTypes.add(ReviewTypes.Example)
        if (word.definition.orEmpty().isNotEmpty() && allWords.filter { it.definition.orEmpty().isNotEmpty() }.size > 4) reviewTypes.add(ReviewTypes.Word)
        if (word.images.orEmpty().isNotEmpty()) reviewTypes.add(ReviewTypes.Image)

        if (reviewTypes.isEmpty()) reviewTypes.add(ReviewTypes.Simple)
        LazyColumn(modifier = Modifier.padding(paddingValues)) {

            when (val reviewType = reviewTypes.random()) {

                ReviewTypes.Simple -> {
                    item {
                        Text(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .padding(horizontal = 24.dp),
                            text = stringResource(id = R.string.do_you_remeber_this_word),
                            style = fontRegular14(
                                MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    item {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(top = 32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Row(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = word.word.orEmpty(),
                                    style = fontMedium24(MaterialTheme.colorScheme.onSurface)
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp),
                                    text = word.type.orEmpty(),
                                    style = fontRegular14(MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }


                            if (word.translation.orEmpty().isNotEmpty()) {
                                Text(
                                    modifier = Modifier.padding(top = 8.dp),
                                    text = word.translation.orEmpty(),
                                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            Text(
                                modifier = Modifier.padding(top = 8.dp),
                                text = word.pronunciation.orEmpty(),
                                style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                            )

                            Text(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .padding(horizontal = 16.dp)
                                    .align(Alignment.Start),
                                text = word.definition.orEmpty(),
                                style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
                            )

                        }

                        CustomButton(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth(),
                            text = stringResource(R.string.yes),
                            style = ButtonStyle.Contained,
                            type = ButtonType.Success,
                            size = ButtonSize.MD
                        ) {
                            word.revisionCount = word.revisionCount + 1
                            word.lastRevisionTime = System.currentTimeMillis()
                            wordViewModel.editWord(word.id, word)
                            rejectedList.add(word)
                            isCorrect = true
                            isCorrect = null
                        }

                        CustomButton(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth(),
                            text = stringResource(R.string.no),
                            style = ButtonStyle.TextOnly,
                            type = ButtonType.Danger,
                            size = ButtonSize.MD
                        ) {
                            rejectedList.add(word)
                            isCorrect = false
                            isCorrect = null
                        }
                    }
                }

                ReviewTypes.Definition,
                ReviewTypes.Sound,
                ReviewTypes.Image,
                ReviewTypes.Word,
                ReviewTypes.Example-> {

                    if (isCorrect == null) {
                        wordSelected = ""

                        val finalList = if (reviewType == ReviewTypes.Word){
                            correctAnswer = word.definition.orEmpty()
                            words.filter { it.definition.orEmpty().isNotEmpty() }.shuffled().take(3).toMutableList()
                        }else{
                            correctAnswer = word.word.orEmpty()
                            words.shuffled().take(3).toMutableList()
                        }
                        finalList.add(word)
                        finalList.shuffle()
                        shuffledList = finalList.shuffled()
                    }

                    when (reviewType) {

                        ReviewTypes.Definition -> {
                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                        .padding(top = 16.dp, bottom = 24.dp),
                                    text = word.definition.orEmpty(),
                                    style = fontMedium14(MaterialTheme.colorScheme.onBackground)
                                )
                            }
                        }

                        ReviewTypes.Example -> {
                            val example = word.example!!.filter { it.contains(word.word.orEmpty()) }.shuffled().random()
                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                        .padding(top = 16.dp, bottom = 24.dp),
                                    text = example.replace(word.word.orEmpty(), " ..... "),
                                    style = fontMedium14(MaterialTheme.colorScheme.onBackground)
                                )
                            }
                        }

                        ReviewTypes.Sound -> {

                            item {
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (word.voice.orEmpty().isNotEmpty()) {
                                        IconButton(onClick = {
                                            val audioFile = File(word.voice!!)
                                            if (player.isPlaying()) return@IconButton
                                            player.playFile(audioFile ?: return@IconButton)
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.icons8_speaker_1),
                                                contentDescription = "SPEAKER",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }


                                    if (word.sound.orEmpty().isNotEmpty()) {
                                        IconButton(onClick = {
                                            val mp = MediaPlayer()
                                            try {
                                                scope.launch {
                                                    mp.setDataSource(word.sound)
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                        mp.prepare()
                                                    }
                                                    mp.setOnPreparedListener {
                                                        mp.start()
                                                    }
                                                }
                                            } catch (e: IOException) {
                                                timber("ERR ::: ${e.printStackTrace()}")
                                            }
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.icons8_speaker_1),
                                                contentDescription = "SPEAKER",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    if (word.pronunciation != null) {
                                        Text(
                                            modifier = Modifier.padding(start = 16.dp),
                                            text = word.pronunciation.toPronunciation(),
                                            style = fontMedium14(
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                        }

                        ReviewTypes.Image -> {
                            try {
                                item {
                                    Row {
                                        SelectableImage(modifier = Modifier.weight(2f).padding(24.dp),path = word.images!!.first(), onClick = {
//                                            navController.navigate(Screens.FullScreenImageScreen.name+"?path=$it")
                                        })

                                        Spacer(modifier = Modifier.weight(2f))
                                    }
                                }
                            }catch (e: Exception){
                                e.printStackTrace()
                            }
                        }

                        ReviewTypes.Word -> {
                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                        .padding(top = 16.dp, bottom = 24.dp),
                                    text = word.word.orEmpty(),
                                    style = fontMedium14(MaterialTheme.colorScheme.onBackground)
                                )
                            }
                        }

                        else -> {

                        }

                    }

                    items(shuffledList) { wordItem ->
                        SimpleReviewItem(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .padding(horizontal = 24.dp),
                            text = if (reviewType == ReviewTypes.Word) wordItem.definition.orEmpty() else wordItem.word.orEmpty(),
                            isCorrect = if (
                                (reviewType == ReviewTypes.Word && wordItem.definition == wordSelected) ||
                                wordItem.word == wordSelected) isCorrect.orFalse() else null,
//                            clickEnabled = isCorrect != null
                        ) {
                            wordSelected = it
                            isCorrect = it == correctAnswer
                            showSheet = true
                        }
                    }
                }

                else -> {

                }

            }

        }
    } else {
        rejectedList.clear()
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheet(isCorrect: Boolean, answer: String, onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    val correctTitles = listOf(
        "Good",
        "Good job!",
        "Excellent!",
        "Well done!",
        "Fantastic!",
        "Outstanding!",
        "Terrific!",
        "Marvelous!",
        "Bravo!",
        "Superb!",
        "Perfect!",
        "Amazing!",
        "Impressive!",
        "Wonderful!",
        "Awesome!",
        "Great work!",
        "Exceptional!",
        "Brilliant!",
        "Remarkable!",
        "Superbly done!",
        "You nailed it!",
    )

    val wrongTitles = listOf(
        "Oops, try again!",
        "Not quite right, give it another shot!",
        "That's incorrect, keep going!",
        "Nice try, but not the right answer.",
        "Incorrect, but don't give up!",
        "Almost there, but not quite!",
        "Not the correct choice, try another!",
        "Uh-oh, wrong answer. Keep trying!",
        "Not the one, but keep going!",
        "Incorrect, but keep pushing!",
        "That's a miss, try a different option!",
        "Incorrect, but you're getting closer!",
        "Not right this time, try again!",
        "Wrong answer, but don't be discouraged!",
        "Nice effort, but not the correct answer.",
        "Incorrect, but keep on trying!",
        "That's a miss, try another option!",
        "Not quite right, keep on going!",
        "Wrong, but you can do better!",
        "Not the correct choice, but keep at it!"
    )

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isCorrect) correctTitles.random() else wrongTitles.random(),
                style = fontBold14(
                    if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer else
                        MaterialTheme.colorScheme.error
                )
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                text = stringResource(id = R.string.correct_was, answer),
                style = fontMedium14(
                    if (isCorrect) MaterialTheme.colorScheme.primary else
                        MaterialTheme.colorScheme.error
                )
            )

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 24.dp),
                text = stringResource(id = R.string.next), size = ButtonSize.XL,
                type = if (isCorrect) ButtonType.Primary else ButtonType.Danger
            ) {
                onDismiss.invoke()
            }
        }

    }
}