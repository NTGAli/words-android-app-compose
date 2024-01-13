package com.ntg.mywords.screens

import android.content.Context
import android.content.res.XmlResourceParser
import androidx.compose.animation.AnimatedVisibility
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.ntg.mywords.R
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.components.*
import com.ntg.mywords.model.Failure
import com.ntg.mywords.model.Success
import com.ntg.mywords.model.VerbData
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.model.db.VerbForms
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.model.response.Definition
import com.ntg.mywords.model.response.DefinitionText
import com.ntg.mywords.model.response.DefinitionX
import com.ntg.mywords.model.response.Meaning
import com.ntg.mywords.model.response.PronunciationVocab
import com.ntg.mywords.model.response.SenseItem
import com.ntg.mywords.model.response.WordDataItem
import com.ntg.mywords.model.then
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.util.*
import com.ntg.mywords.vm.WordViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import kotlin.reflect.javaType
import kotlin.reflect.typeOf


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
    var example = ""


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
                wordViewModel = wordViewModel,
                wordData = {
                    wordData = it
                },
                exampleField = {
                    example = it
                }
            )

        }, bottomBar = {
            BottomBarContent(wordId != -1) {
                timber("sklfjelkjflkejf $example")
                if (example !in wordData.example.orEmpty() && example.isNotEmpty()) {
                    val ex: MutableList<String> = wordData.example as MutableList<String>
                    ex.add(example)
                    wordData.example = ex.toList()
                }
                submitWord(wordData, wordViewModel, context, wordId != -1, navController)
            }
        }
    )

}


@Composable
private fun BottomBarContent(isEdit: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(start = 32.dp, end = 32.dp)
    ) {
        Divider(Modifier.padding(bottom = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
        CustomButton(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            text = if (isEdit) stringResource(id = R.string.edit) else stringResource(id = R.string.insert),
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordEdit: Word?,
    wordViewModel: WordViewModel,
    wordData: (Word) -> Unit,
    exampleField: (String) -> Unit
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

    val dictionaryApi = remember {
        mutableIntStateOf(0)
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

    var openVoiceToSpeech by remember {
        mutableStateOf(false)
    }

    var voiceForWord by remember {
        mutableStateOf(false)
    }

    var definitionForWord by remember {
        mutableStateOf(false)
    }

    var exampleForWord by remember {
        mutableStateOf(false)
    }

    val exampleList = remember {
        mutableStateListOf<String>()
    }

    val definitionList = remember {
        mutableStateListOf<Definition>()
    }

    val definitionListFreeApi = remember {
        mutableStateListOf<DefinitionX>()
    }

    val definitionListApi = remember {
        mutableStateListOf<DefinitionX>()
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val listId = wordViewModel.getIdOfListSelected().observeAsState().value?.id

    timber("LIST_ID_SELECTED :::: $listId")


    OpenVoiceSearch(launch = openVoiceToSpeech, voiceSearch = {
        if (it != null) {
            when {
                voiceForWord -> {
                    word.value = it
                    voiceForWord = false
                }

                definitionForWord -> {
                    definition.value = it
                    definitionForWord = false
                }

                exampleForWord -> {
                    example.value = it
                    exampleForWord = false
                }
            }
        }
        openVoiceToSpeech = false
    })


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

    var visible by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val phonetics = listOf(
        "ɪ",
        "ɛ",
        "æ",
        "ʌ",
        "ə",
        "ɚ",
        "ʊ",
        "ɔ",
        "ɑ",
        "ɑɪ",
        "ɑʊ",
        "ɔɪ",
        "p",
        "ɵ",
        "ð",
        "ʃ",
        "ʒ",
        "ʧ",
        "ʤ",
        "ŋ",
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


    val raw = context.resources.openRawResource(R.raw.word_forms)
    val writer: Writer = StringWriter()
    val buffer = CharArray(1024)
    raw.use { rawData ->
        val reader: Reader = BufferedReader(InputStreamReader(rawData, "UTF-8"))
        var n: Int
        while (reader.read(buffer).also { n = it } != -1) {
            writer.write(buffer, 0, n)
        }
    }
    val jsonString = writer.toString()
    val itemType = object : TypeToken<List<VerbData>>() {}.type
    val verbList = Gson().fromJson<List<VerbData>>(jsonString, itemType)





    LaunchedEffect(key1 = fetchDataWord.value, block = {

        if (fetchDataWord.value) {

            when (dictionaryApi.intValue) {
                1 -> {
                    wordViewModel.getDataWordFromFreeDictionary(word.value)
                        .observe(lifecycleOwner) {

                            when (it) {
                                is NetworkResult.Error -> {
                                    timber("getDataWordFromFreeDictionary ::: ER ${it.message}")
                                }

                                is NetworkResult.Loading -> {
                                    timber("getDataWordFromFreeDictionary ::: LD")

                                }

                                is NetworkResult.Success -> {
                                    timber("getDataWordFromFreeDictionary ::: SC ::: ${it.data}")
                                    fetchDataWord.value = false
                                    exampleList.clear()
                                    example.value = ""
                                    definition.value = ""

                                    if (it.data != null) {
                                        timber("WORD_DATA_VOCAB :: ${it.data}")
                                        listOfDefinitions.clear()
                                        definitionListFreeApi.clear()

                                        pronunciation.value = it.data[0].phonetic.orEmpty()
//                                        it.data[0].meanings?.filter { it.partOfSpeech == type.value }
//                                            ?.forEach {
//                                                it.definitions.orEmpty().forEach {
//                                                    listOfDefinitions.add(it.definition.orEmpty())
//                                                }
//                                            }

                                        try {
                                            it.data.forEach {
                                                it.meanings?.filter { it.partOfSpeech == type.value }
                                                    ?.forEach {
                                                        it.definitions.orEmpty().forEach {
                                                            listOfDefinitions.add(it.definition.orEmpty())
                                                            definitionListFreeApi.add(it)
                                                        }

                                                    }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

//                                        try {
//                                            it.data[0].meanings.orEmpty()
//                                                .filter { it.partOfSpeech.orEmpty() == type.value }[0].definitions.orEmpty()
//                                                .forEach {
//                                                    definitionListFreeApi.add(it)
//
//                                                }
//                                        } catch (e: Exception) {
//                                            e.printStackTrace()
//                                        }

                                        if (type.value == "verb") {
                                            pastSimple.value =
                                                verbList.first { it.word == word.value }.past_simple.orEmpty()
                                            pastParticiple.value =
                                                verbList.first { it.word == word.value }.pp.orEmpty()
                                        }


                                    } else {
                                        context.toast(context.getString(R.string.not_exist))
                                    }
                                }
                            }

                        }
                }

                2 -> {
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
                                fetchDataWord.value = false
                                exampleList.clear()
                                example.value = ""
                                definition.value = ""
                                if (it.data.orEmpty().isNotEmpty()) {
                                    listOfDefinitions.clear()
                                    wordDataItems = it.data.orEmpty()
                                    pronunciation.value =
                                        it.data?.get(0)?.headwordInformation?.pronunciations?.get(
                                            0
                                        )?.mw.orEmpty()
                                    it.data?.filter { it.functionalLabel == type.value }
                                        ?.forEach {
                                            it.shortDefinitions?.forEach { def ->
                                                listOfDefinitions.add(def)
                                            }
                                        }

//                                    it.data?.forEach {
//                                        it.definitionSection?.forEach {
//                                            it.sseq?.forEach {
//                                                it.forEach {
//                                                    it.forEach {
//
//                                                        timber("ajkaefhjeahfkjehkfjhew ${it}")
//                                                        val gson = Gson()
//                                                        val definition = gson.fromJson(it.toString(), WordDataItem::class.java)
////                                                        try {
////                                                            (it as SenseItem).dt?.forEach {
////                                                                it.forEach {
////                                                                    timber("ajkaefhjeahfkjehkfjhew ${it.text}")
////                                                                }
////                                                            }
////                                                        }catch (e: Exception){
////                                                            e.printStackTrace()
////                                                        }
//
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }

                                    if (type.value == "verb") {
                                        pastSimple.value =
                                            verbList.first { it.word == word.value }.past_simple.orEmpty()
                                        pastParticiple.value =
                                            verbList.first { it.word == word.value }.pp.orEmpty()
                                    }
//                                    if (type.value == "verb") {
//                                        pastSimple.value =
//                                            it.data?.first { it.functionalLabel == type.value }?.inflections?.get(
//                                                0
//                                            )?.infection.orEmpty()
//                                        pastParticiple.value =
//                                            it.data?.first { it.functionalLabel == type.value }?.inflections?.get(
//                                                2
//                                            )?.infection.orEmpty()
//                                    }
                                } else {
                                    context.toast(context.getString(R.string.not_exist))
                                }
                            }
                        }

                    }

                }

                3 -> {
                    wordViewModel.getWord(word.value, type.value).observe(lifecycleOwner) {
                        when (it) {
                            is NetworkResult.Error -> {

                            }

                            is NetworkResult.Loading -> {

                            }

                            is NetworkResult.Success -> {
                                if (it.data != null) {
                                    timber("WORD_DATA_VOCAB :: ${it.data}")
                                    listOfDefinitions.clear()
                                    //                        wordDataItems = it.data.orEmpty()
                                    pronunciation.value =
                                        it.data.data?.pronunciations?.first { it.accent == "am" }?.pronunciation.orEmpty()


                                    it.data.data?.definitions?.sortedBy { it.number }?.forEach {
                                        listOfDefinitions.add(it.definition.orEmpty())
                                        definitionList.add(it)
                                    }

                                    if (type.value == "verb") {
                                        pastSimple.value =
                                            it.data.data?.wordForms?.pastSimple.orEmpty()
                                        pastParticiple.value =
                                            it.data.data?.wordForms?.pastParticiple.orEmpty()
                                    }
                                } else {
                                    context.toast(context.getString(R.string.not_exist))
                                }

                                fetchDataWord.value = false
                            }
                        }
                    }

                }
            }

        }

    })



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
                    .fillMaxWidth(), text = word, label = stringResource(R.string.word),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.microphone_02),
                enabledLeadingIcon = true,
                leadingIconOnClick = {
                    openVoiceToSpeech = true
                    voiceForWord = true
                }
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

                Row(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
                    CustomButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        text = stringResource(id = R.string.auto_fill_from_first),
                        size = ButtonSize.LG,
                        type = ButtonType.Secondary,
                        style = ButtonStyle.Contained
                    ) {
                        fetchDataWord.value = true
                        dictionaryApi.intValue = 1
                    }

                    CustomButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        text = stringResource(id = R.string.auto_fill_from_second),
                        size = ButtonSize.LG,
                        type = ButtonType.Secondary,
                        style = ButtonStyle.Contained
                    ) {
                        fetchDataWord.value = true
                        dictionaryApi.intValue = 2
                    }
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
                enabled = !fetchDataWord.value,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.microphone_02),
                enabledLeadingIcon = true,
                leadingIconOnClick = {
                    openVoiceToSpeech = true
                    definitionForWord = true
                }
            )
        }

        if (listOfDefinitions.isNotEmpty()) {

            item {
                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    text = stringResource(id = R.string.select_a_definition),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            items(listOfDefinitions.take(3)) {
                SampleItem(
                    title = it,
                    enableRadioButton = true,
                    radioSelect = mutableStateOf(definition.value == it),
                    onClick = { text, _, isSelect ->
                        definition.value = text

                        if (dictionaryApi.intValue == 1) {
                            exampleList.clear()
                            if (definitionListFreeApi.first { it.definition == definition.value }.example.orEmpty()
                                    .isNotEmpty()
                            ) {
                                exampleList.add(definitionListFreeApi.first { it.definition == definition.value }.example!!)
                            }
                        } else if (dictionaryApi.intValue == 2) {

                        } else if (dictionaryApi.intValue == 3) {
                            definitionList.first { it.definition == definition.value }.examples?.forEach {
                                exampleList.add(it)
                            }
                        }

                    })

            }

            items(listOfDefinitions.subList(3, listOfDefinitions.size)) {
                AnimatedVisibility(visible = visible) {
                    SampleItem(
                        title = it,
                        enableRadioButton = true,
                        radioSelect = mutableStateOf(definition.value == it),
                        onClick = { text, _, isSelect ->
                            definition.value = text

                            if (dictionaryApi.intValue == 1) {
                                exampleList.clear()
                                if (definitionListFreeApi.first { it.definition == definition.value }.example.orEmpty()
                                        .isNotEmpty()
                                ) {
                                    exampleList.add(definitionListFreeApi.first { it.definition == definition.value }.example!!)
                                }
                            } else if (dictionaryApi.intValue == 2) {

                            } else if (dictionaryApi.intValue == 3) {
                                definitionList.first { it.definition == definition.value }.examples?.forEach {
                                    exampleList.add(it)
                                }
                            }

                        })
                }

            }
            item {
                if (listOfDefinitions.size > 3) {
                    CustomButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (visible) stringResource(id = R.string.show_less) else stringResource(
                            id = R.string.show_more
                        ),
                        style = ButtonStyle.TextOnly,
                        type = ButtonType.Secondary,
                        size = ButtonSize.MD
                    ) {
                        visible = !visible
                    }
                }
            }

            item() {

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

                    }
                },
                onChange = {
                    exampleField.invoke(it)
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