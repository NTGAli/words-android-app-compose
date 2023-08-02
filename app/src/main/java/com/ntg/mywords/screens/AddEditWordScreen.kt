package com.ntg.mywords.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.*
import com.ntg.mywords.model.Failure
import com.ntg.mywords.model.Success
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.model.then
import com.ntg.mywords.ui.theme.Secondary100
import com.ntg.mywords.util.notEmptyOrNull
import com.ntg.mywords.util.notFalse
import com.ntg.mywords.util.timber
import com.ntg.mywords.util.toast
import com.ntg.mywords.vm.WordViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWordScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    wordId: Int? = null
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    var wordData = Word()


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.add_new),
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->


            timber("akwldklwadklwmakdlm $wordId")

            val word = wordViewModel.findWord(wordId)?.observeAsState()
            Content(
                paddingValues = innerPadding,
                id = wordId,
                word = mutableStateOf(word?.value?.word ?: ""),
                type = mutableStateOf(word?.value?.type ?: ""),
                pronunciation = mutableStateOf(word?.value?.pronunciation ?: ""),
                definition = mutableStateOf(word?.value?.definition ?: ""),
                exampleListWord = word?.value?.example.orEmpty(),
                lastRevision = word?.value?.lastRevisionTime ?: System.currentTimeMillis(),
                dateCreated = word?.value?.dateCreated ?: System.currentTimeMillis(),

            ) {
                wordData = it
            }

        }, bottomBar = {
            BottomBarContent {
                submitWord(wordData, wordViewModel, context, wordId != -1, navController)
            }
        }
    )

}


@Composable
private fun BottomBarContent(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(start = 32.dp, end = 32.dp)
            .background(Color.White)
    ) {
        Divider(Modifier.padding(bottom = 16.dp), color = Secondary100)
        CustomButton(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "button",
            size = ButtonSize.XL
        ) {
            onClick.invoke()
        }

    }
}

private fun submitWord(
    wordData: Word,
    wordViewModel: WordViewModel,
    context: Context,
    isEdit: Boolean,
    navController: NavController
) {
    val result = notEmptyOrNull(wordData.word, context.getString(R.string.err_word_required))
        .then { notEmptyOrNull(wordData.type, context.getString(R.string.err_type_required)) }
        .then {
            notEmptyOrNull(
                wordData.example.toString(),
                context.getString(R.string.err_example_required)
            )
        }
        .then {
            notFalse(
                !wordViewModel.checkIfExist(
                    wordData.word.orEmpty(),
                    wordData.type.orEmpty()
                ), context.getString(R.string.err_word_already_exist)
            )
        }

    when (result) {

        is Success -> {

            if (isEdit) {
                wordViewModel.editWord(wordData.id, wordData)
            } else {
                wordViewModel.addNewWord(wordData)
            }
            navController.popBackStack()
        }

        is Failure -> {
            context.toast(result.errorMessage)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    paddingValues: PaddingValues,
    id: Int? = null,
    word: MutableState<String> = remember { mutableStateOf("") },
    pronunciation: MutableState<String> = remember { mutableStateOf("") },
    type: MutableState<String> = remember { mutableStateOf("") },
    definition: MutableState<String> = remember { mutableStateOf("") },
    exampleListWord: List<String> = listOf(),
    lastRevision: Long = System.currentTimeMillis(),
    dateCreated: Long = System.currentTimeMillis(),
    wordData: (Word) -> Unit
) {

    val a = remember { mutableStateListOf<String>() }

    timber("awsejfhkjehskjfhekjf --")

//    val word = remember {
//        mutableStateOf(wordForEdit?.word.orEmpty())
//    }
//    val pronunciation = remember {
//        mutableStateOf(wordForEdit?.pronunciation.orEmpty())
//    }
//    val type = remember {
//        mutableStateOf(wordForEdit?.type.orEmpty())
//    }
//    val definition = remember {
//        mutableStateOf(wordForEdit?.definition.orEmpty())
//    }
    val example = remember {
        mutableStateOf("")
    }
//
    val exampleList = remember {
        mutableStateListOf<String>()
    }

    if (exampleList.isEmpty()){
        exampleListWord.forEach{
            exampleList.add(it)
        }
    }






    wordData(
        Word(
            if (id != null && id != -1) id else 0,
            word = word.value,
            type = type.value,
            pronunciation = pronunciation.value,
            definition = definition.value,
            example = exampleList,
            dateCreated = dateCreated,
            lastRevisionTime = lastRevision
        )
    )

    val skipPartiallyExpanded by remember { mutableStateOf(true) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )


    var openBottomSheet by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val phonetics = listOf<String>(
        "ɛ",
        "æ",
        "ʌ",
        "ə",
        "ɚ",
        "ʊ",


        )

    val typeWordItems = arrayListOf(
        "noun",
        "verb",
        "adjective",
        "adverb",
        "pronoun",
        "determiner",
        "preposition",
        "conjunction"
    )

    if (openBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {


            LazyColumn(Modifier.padding(6.dp)) {

                items(typeWordItems) {
                    SampleItem(title = it) { title, _ ->
                        type.value = title
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet = false
                            }
                        }
                    }
                }

            }
        }
    }



    LazyColumn(
        modifier = Modifier.padding(
            top = paddingValues.calculateTopPadding(),
            start = 32.dp,
            end = 32.dp,
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {

        item {
            EditText(
                Modifier
                    .fillMaxWidth(), text = word, label = stringResource(R.string.word)
            )
            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = pronunciation,
                label = stringResource(R.string.pronunciation)
            )


        }


        item {
            LazyRow {
                items(phonetics) {
                    ItemText(
                        modifier = Modifier.padding(end = 8.dp, top = 8.dp),
                        text = it,
                        onClick = {
                            pronunciation.value = pronunciation.value + it
                        })
                }
            }
        }

        item {
            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = type,
                label = stringResource(R.string.type),
                readOnly = true,
                onClick = {
                    openBottomSheet = true
                }
            )
            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(), text = definition, label = stringResource(R.string.definition)
            )
            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = example,
                label = stringResource(R.string.example),
                enabledLeadingIcon = true,
                leadingIconOnClick = {
                    if (it.isNotEmpty()) {
                        exampleList.add(it)
                        example.value = ""

//                        example.value = example.value+"kk"?
                    }
                }
            )
        }

        items(exampleList.reversed()) {
            timber("LIST_DATA", "$exampleList")
            SampleItem(title = it) { title, _ ->
                exampleList.remove(title)
            }
        }

    }


}