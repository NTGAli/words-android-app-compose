package com.ntg.mywords.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.components.*
import com.ntg.mywords.model.Failure
import com.ntg.mywords.model.Success
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.model.db.VerbForms
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.model.response.WordDataItem
import com.ntg.mywords.model.then
import com.ntg.mywords.util.*
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
                title = stringResource(R.string.add_new_word),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->

            val word = wordViewModel.findWord(wordId)?.observeAsState()
            Content(
                paddingValues = innerPadding,
                wordEdit = word?.value,
                wordViewModel = wordViewModel
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
    ) {
        Divider(Modifier.padding(bottom = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
        CustomButton(
            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
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
                ) || isEdit, context.getString(R.string.err_word_already_exist)
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
    wordEdit: Word?,
    wordViewModel: WordViewModel,
    wordData: (Word) -> Unit
) {

    val word = remember {
        mutableStateOf("")
    }

    val translation = remember {
        mutableStateOf("")
    }

    val pronunciation = remember {
        mutableStateOf("")
    }

    val type = remember {
        mutableStateOf("")
    }

    val pastSimple = remember {
        mutableStateOf("")
    }

    val pastParticiple = remember {
        mutableStateOf("")
    }

    val definition = remember {
        mutableStateOf("")
    }

    val example = remember {
        mutableStateOf("")
    }

    val applyEdit = remember {
        mutableStateOf(false)
    }

    val fetchDataWord = remember {
        mutableStateOf(false)
    }

    val exampleList = remember {
        mutableStateListOf<String>()
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val listId = wordViewModel.getIdOfListSelected().observeAsState().value?.id



    if (wordEdit != null && !applyEdit.value) {
        word.value = wordEdit.word.orEmpty()
        translation.value = wordEdit.translation.orEmpty()
        type.value = wordEdit.type.orEmpty()
        pronunciation.value = wordEdit.pronunciation.orEmpty()
        pastSimple.value = wordEdit.verbForms?.pastSimple.orEmpty()
        pastParticiple.value = wordEdit.verbForms?.pastParticiple.orEmpty()
        definition.value = wordEdit.definition.orEmpty()
        wordEdit.example?.forEach {
            exampleList.add(it)
        }
        applyEdit.value = true
    }

    wordData(
        Word(
            if (wordEdit?.id != null && wordEdit.id != -1) wordEdit.id else 0,
            listId = listId.orZero(),
            word = word.value,
            type = type.value,
            verbForms = VerbForms(
                pastSimple = pastSimple.value,
                pastParticiple = pastParticiple.value
            ),
            translation = translation.value,
            pronunciation = pronunciation.value,
            definition = definition.value,
            example = exampleList,
            dateCreated = wordEdit?.dateCreated ?: System.currentTimeMillis(),
            lastRevisionTime = wordEdit?.lastRevisionTime ?: System.currentTimeMillis()
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

    val phonetics = listOf(
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

    var wordDataItems = listOf<WordDataItem>()

    val listOfDefinitions = remember {
        mutableStateListOf<String>()
    }

    if (openBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {


            LazyColumn(Modifier.padding(6.dp)) {

                items(typeWordItems) {
                    SampleItem(title = it) { title, _, _ ->
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


    if (fetchDataWord.value) {

        wordViewModel.getDataWord(word.value).observe(lifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    timber("WORD_DATA :: ERR ${it.message}")
                    context.toast(context.getString(R.string.error_occurred))
                    fetchDataWord.value = false
                }
                is NetworkResult.Loading -> {
                    timber("WORD_DATA ::  LD")
                }
                is NetworkResult.Success -> {
                    timber("WORD_DATA :: ${it.data}")
                    if (it.data.orEmpty().isNotEmpty()) {
                        listOfDefinitions.clear()
                        wordDataItems = it.data.orEmpty()
                        pronunciation.value =
                            it.data?.get(0)?.headwordInformation?.pronunciations?.get(0)?.mw.orEmpty()
                        it.data?.filter { it.functionalLabel == type.value }?.forEach {
                            it.shortDefinitions?.forEach { def ->
                                listOfDefinitions.add(def)
                            }
                        }
                        if (type.value == "verb") {
                            pastSimple.value =
                                it.data?.first { it.functionalLabel == type.value }?.inflections?.get(
                                    0
                                )?.infection.orEmpty()
                            pastParticiple.value =
                                it.data?.first { it.functionalLabel == type.value }?.inflections?.get(
                                    1
                                )?.infection.orEmpty()
                        }
                    } else {
                        context.toast(context.getString(R.string.not_exist))
                    }
                    fetchDataWord.value = false
                }
            }

        }
    }


//    wordDataItems.first { it.shortDefinitions?.contains(definition.value).orFalse() }.


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
                text = type,
                label = stringResource(R.string.type),
                readOnly = true,
                onClick = {
                    openBottomSheet = true
                }
            )

            if (word.value.isNotEmpty() && type.value.isNotEmpty()) {
                CustomButton(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp),
                    text = stringResource(id = R.string.auto_fill),
                    size = ButtonSize.SM,
                    type = ButtonType.Primary,
                    style = ButtonStyle.TextOnly
                ) {
                    fetchDataWord.value = true
                }
            }

            if (type.value == "verb") {
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    EditText(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        text = pastSimple,
                        label = stringResource(id = R.string.past_simple),
                        enabled = !fetchDataWord.value
                    )
                    EditText(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        text = pastParticiple,
                        label = stringResource(id = R.string.past_participle),
                        enabled = !fetchDataWord.value
                    )
                }
            }

            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = translation,
                label = stringResource(R.string.translation)
            )
            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = pronunciation,
                label = stringResource(R.string.pronunciation),
                enabled = !fetchDataWord.value
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
                    .fillMaxWidth(), text = definition, label = stringResource(R.string.definition),
                enabled = !fetchDataWord.value
            )
        }

        if (listOfDefinitions.isNotEmpty()) {

            items(listOfDefinitions) {
                SampleItem(
                    title = it,
                    enableRadioButton = true,
                    radioSelect = mutableStateOf(definition.value == it),
                    onClick = { text, _, isSelect ->
                        definition.value = text
                    })
            }

        }

        item {
            EditText(
                Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
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
            SampleItem(title = it) { title, _, _ ->
                exampleList.remove(title)
            }
        }

    }
}