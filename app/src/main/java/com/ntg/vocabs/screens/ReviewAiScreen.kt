package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.AiTextField
import com.ntg.vocabs.components.AiTextFieldStateStates
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.rememberAiTextFieldState
import com.ntg.vocabs.model.ReviewTypes
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.playback.AndroidAudioPlayer
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.util.getStateRevision
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewAiScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var left by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.review_with_ai),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() },
                endText = stringResource(id = R.string.left_format, left.toString())
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, wordViewModel, navController) {
                left = it
            }

        }
    )

}


@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    navController: NavController,
    onLeftChange: (Int) -> Unit
) {

    val reviewTypes: ArrayList<ReviewTypes> = arrayListOf()

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

    var word by remember {
        mutableStateOf<Word?>(null)
    }

    var wordSelected by remember {
        mutableStateOf("")
    }

    var shuffledList by remember {
        mutableStateOf<List<Word>>(listOf())
    }

    var words by remember {
        mutableStateOf<List<Word>>(listOf())
    }

    var reviewType by remember {
        mutableStateOf<ReviewTypes?>(null)
    }

    var showSheet by remember { mutableStateOf(false) }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val aiTextFiledState = rememberAiTextFieldState()

    val listId = wordViewModel.currentList().observeAsState().value?.id
    val allWords =
        wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value.orEmpty()

    if (words.isEmpty()) {
        words =
            allWords
                .filter {
                getStateRevision(
                    it.revisionCount,
                    it.lastRevisionTime
                ) == 2 || getStateRevision(
                    it.revisionCount,
                    it.lastRevisionTime
                ) == 3
            }
    }


    val rejectedList = remember {
        mutableStateListOf<Word>()
    }


    words = words.filterNot { it in rejectedList }
    onLeftChange.invoke(words.size)


    val userQuery = remember {
        mutableStateOf("")
    }

    if (words.isNotEmpty()) {

        if (word == null) {
            word = words[0]
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 32.dp),
                text = stringResource(
                    id = R.string.maje_sentence_with_format,
                    word?.word.orEmpty(),
                    word?.type.orEmpty()
                ),
                style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
            )

            AiTextField(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 32.dp),
                text = userQuery,
                placeHolder = "",
                state = aiTextFiledState
            )


            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 16.dp),
                text = stringResource(id = R.string.chack), size = ButtonSize.MD,
                enable = true
            ) {
                isLoading = !isLoading
                if (isLoading) aiTextFiledState.state =
                    AiTextFieldStateStates.Default else aiTextFiledState.state =
                    AiTextFieldStateStates.Generating

                wordViewModel.checkUserSentenceAI(userQuery.value)
            }
        }

    }

}