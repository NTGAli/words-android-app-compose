package com.ntg.vocabs.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ntg.vocabs.R
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.ButtonIcon
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.DescriptionType
import com.ntg.vocabs.components.DividerLine
import com.ntg.vocabs.components.EditText
import com.ntg.vocabs.components.ItemOption
import com.ntg.vocabs.components.ItemText
import com.ntg.vocabs.components.NeedProDialog
import com.ntg.vocabs.components.OpenVoiceSearch
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.model.Failure
import com.ntg.vocabs.model.Success
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.model.db.GermanNouns
import com.ntg.vocabs.model.db.VerbForms
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.model.response.Definition
import com.ntg.vocabs.model.response.DefinitionX
import com.ntg.vocabs.model.then
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.playback.AndroidAudioPlayer
import com.ntg.vocabs.record.AndroidAudioRecorder
import com.ntg.vocabs.ui.theme.Warning300
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.util.CompressImage
import com.ntg.vocabs.util.generateUniqueFiveDigitId
import com.ntg.vocabs.util.getFormattedTimestamp
import com.ntg.vocabs.util.getSubdirectory
import com.ntg.vocabs.util.notEmptyOrNull
import com.ntg.vocabs.util.orDefault
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orTrue
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.util.setReviewNotification
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.PermissionViewModel
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWordScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel,
    wordId: Int? = null
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    var wordData by remember {
        mutableStateOf(Word())
    }
    var example = ""

    var email by remember {
        mutableStateOf<String?>(null)
    }

    loginViewModel.getUserData().collectAsState(initial = null).value.let { dataSettings ->
        if (dataSettings?.email != null) {
            email = dataSettings.email
        }
    }

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
                navController = navController,
                wordEdit = word?.value,
                wordViewModel = wordViewModel,
                loginViewModel = loginViewModel,
                email,
                wordData = {
                    wordData = it
                },
                exampleField = {
                    example = it
                }
            )

        }, bottomBar = {

            BottomBarContent(wordId != -1) {
                if (example !in wordData.example.orEmpty() && example.trim().isNotEmpty()) {
                    val ex: MutableList<String> = wordData.example as MutableList<String>
                    ex.add(example)
                    wordData.example = ex.toList()
                }
                submitWord(wordData, wordViewModel, context, wordId != -1, email, navController)

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
    email: String?,
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


    when (result) {

        is Success -> {
            if (isEdit) {
                wordViewModel.editWord(wordData.id, wordData)
            } else {
                wordViewModel.addNewWord(wordData.apply { id = generateUniqueFiveDigitId() })
                timber("getUnSyncedWords ::::: $email")


                setReviewNotification(context, wordData.word.orEmpty(), 1)
            }

            navController.popBackStack()
        }

        is Failure -> {
            context.toast(result.errorMessage)
        }

    }
}


private var recorder: AndroidAudioRecorder? = null

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    wordEdit: Word?,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel,
    email: String?,
    wordData: (Word) -> Unit,
    exampleField: (String) -> Unit
) {


    val context = LocalContext.current

    val language = wordViewModel.currentList().observeAsState().value?.language

    val word = rememberSaveable {
        mutableStateOf("")
    }

    if (recorder == null) {
        recorder = AndroidAudioRecorder(context)
    }

    val player by lazy {
        AndroidAudioPlayer(context)
    }


    var audioFile by rememberSaveable {
        mutableStateOf<File?>(null)
    }

    var audioFileName by rememberSaveable {
        mutableStateOf("")
    }

    var imagePath by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var imageUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(key1 = Unit, block = {
        audioFileName = System.currentTimeMillis().toString()
    })

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri == null || uri.toString().isEmpty()) return@rememberLauncherForActivityResult
            val compressImage = CompressImage(context)
            bitmap = compressImage.compressImage(uri.toString())
            imagePath = saveImageInFolder(bitmap, context, word.value).path
        })

    val translation = rememberSaveable {
        mutableStateOf("")
    }

    val pronunciation = rememberSaveable {
        mutableStateOf("")
    }

    val type = rememberSaveable {
        mutableStateOf("")
    }

    val article = rememberSaveable {
        mutableStateOf("")
    }

    val plural = rememberSaveable {
        mutableStateOf("")
    }

    val pastSimple = rememberSaveable {
        mutableStateOf("")
    }

    val pastParticiple = rememberSaveable {
        mutableStateOf("")
    }

    val definition = rememberSaveable {
        mutableStateOf("")
    }

    val soundUrl = rememberSaveable {
        mutableStateOf("")
    }

    var synonym = rememberSaveable {
        mutableStateOf("")
    }

    val antonyms = rememberSaveable {
        mutableStateOf("")
    }

    val dictionaryApi = rememberSaveable {
        mutableIntStateOf(0)
    }

    val example = rememberSaveable {
        mutableStateOf("")
    }

    val applyEdit = rememberSaveable {
        mutableStateOf(false)
    }

    val fetchDataWord = rememberSaveable {
        mutableStateOf(false)
    }

    var openVoiceToSpeech by rememberSaveable {
        mutableStateOf(false)
    }

    var voiceForWord by rememberSaveable {
        mutableStateOf(false)
    }

    var definitionForWord by rememberSaveable {
        mutableStateOf(false)
    }

    var exampleForWord by rememberSaveable {
        mutableStateOf(false)
    }

    var openDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var descriptionType by rememberSaveable {
        mutableStateOf(DescriptionType.IMAGE)
    }

    val exampleList = remember {
        mutableStateListOf<String>()
    }

    val definitionList = remember {
        mutableStateListOf<Definition>()
    }

    val definitionListData = rememberSaveable {
        mutableStateOf<ArrayList<Definition>>(arrayListOf())
    }

    val similarWords = rememberSaveable {
        mutableStateOf<List<Word>>(listOf())
    }

    if (definitionListData.value.size > definitionList.size && definitionList.isEmpty()) {
        definitionList.addAll(definitionListData.value)
    } else {
        definitionListData.value.clear()
        definitionListData.value.addAll(definitionList)
    }

    val definitionListFreeApi = remember {
        mutableStateListOf<DefinitionX>()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val listId = wordViewModel.currentList().observeAsState().value?.id

    val isPurchased =
        loginViewModel.getUserData().collectAsState(initial = null).value?.isPurchased.orFalse()

    val isAllowThirdDictionary =
        loginViewModel.getUserData().collectAsState(initial = null).value?.allowThirdDictionary.orTrue()

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
            if (it.trim().isNotEmpty()) {
                exampleList.add(it)
            }
        }
        if (wordEdit.synonyms.orEmpty().isNotEmpty()) {
            synonym.value = wordEdit.synonyms.toString().drop(1).dropLast(1)
        }

        if (wordEdit.antonyms.orEmpty().isNotEmpty()) {
            antonyms.value = wordEdit.antonyms.toString().drop(1).dropLast(1)
        }

        if (wordEdit.images.orEmpty().isNotEmpty()) {
            imagePath = wordEdit.images.orEmpty().first()
        }

        if (wordEdit.voice.orEmpty().isNotEmpty()) {
            audioFile = File(wordEdit.voice!!)
        }

        if (wordEdit.sound.orEmpty().isNotEmpty()) {
            soundUrl.value = wordEdit.sound.orEmpty()
        }

        applyEdit.value = true
    }

    if (wordEdit == null) {
        similarWords.value = wordViewModel.findWord(word.value, type.value)
            ?.observeAsState(initial = listOf())?.value.orEmpty().toList()
    }

    wordData(
        Word(
            if (wordEdit?.id != null && wordEdit.id != -1) wordEdit.id else 0,
            fid = wordEdit?.fid,
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
            lastRevisionTime = wordEdit?.lastRevisionTime ?: System.currentTimeMillis(),
            article = article.value,
            plural = plural.value,
            voice = audioFile?.path,
            sound = soundUrl.value,
            images = if (imagePath.orEmpty().isEmpty()) null else listOf(imagePath.orEmpty()),
            synonyms = if (synonym.value.isNotEmpty()) synonym.value.split(",") else null,
            antonyms = if (antonyms.value.isNotEmpty()) antonyms.value.split(",") else null,
            voiceSynced = if (audioFile == null) null else false,
            imageSynced = if (imagePath.orEmpty().isNotEmpty()) false else null,
            synced = false
        )
    )

    val skipPartiallyExpanded by remember { mutableStateOf(true) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )


    var openBottomSheet by remember {
        mutableStateOf(false)
    }

    var openArticleBottomSheet by remember {
        mutableStateOf(false)
    }

    var openCaseBottomSheet by remember {
        mutableStateOf(false)
    }

    var visible by rememberSaveable {
        mutableStateOf(false)
    }

    var micStart by remember {
        mutableStateOf(false)
    }

    var fillGermanNoun by rememberSaveable {
        mutableStateOf(false)
    }

    var germanNoun by rememberSaveable {
        mutableStateOf<GermanNouns?>(null)
    }


    var allowRecording by rememberSaveable {
        mutableStateOf(false)
    }

    if (language == "German" && wordEdit == null && word.value.isNotEmpty() &&
        type.value == "noun" && plural.value.isEmpty() && article.value.isEmpty() ||
        fillGermanNoun
    ) {
        val pureWord = when {
            word.value.startsWith("die", ignoreCase = true) -> word.value.removePrefix("die").trim()
            word.value.startsWith("das", ignoreCase = true) -> word.value.removePrefix("das").trim()
            word.value.startsWith("der", ignoreCase = true) -> word.value.removePrefix("der").trim()
            else -> word.value
        }
        germanNoun = wordViewModel.germanNoun(pureWord).observeAsState().value
        article.value = when (germanNoun?.genus) {
            "f" -> "die"
            "m" -> "der"
            "n" -> "das"
            else -> ""
        }
        plural.value = germanNoun?.plural.orEmpty()
        fillGermanNoun = false
    }

    allowRecording =
        rememberPermissionState(Manifest.permission.RECORD_AUDIO).status.isGranted

    val permissionViewModel = viewModel<PermissionViewModel>()

    val micPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionViewModel.onPermissionResult(
                permission = Manifest.permission.RECORD_AUDIO,
                isGranted = isGranted
            )
            allowRecording = isGranted
        }
    )


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

                item {
                    Spacer(modifier = Modifier.padding(24.dp))
                }

            }
        }
    }


    if (openArticleBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { openArticleBottomSheet = false },
            sheetState = bottomSheetState,
        ) {


            LazyColumn(Modifier.padding(6.dp)) {

                items(
                    listOf(
                        "die",
                        "das",
                        "der"
                    )
                ) {
                    SampleItem(title = it) { title, _, _ ->
                        article.value = title
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openArticleBottomSheet = false
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.padding(16.dp))
                }

            }
        }
    }


    if (openCaseBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { openCaseBottomSheet = false },
            sheetState = bottomSheetState,
        ) {


            LazyColumn(Modifier.padding(6.dp)) {

                items(
                    listOf(
                        "Nominativ",
                        "Genitiv",
                        "Dativ",
                        "Akkusativ"
                    )
                ) {
                    SampleItem(title = it) { title, _, _ ->
                        article.value = title
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openCaseBottomSheet = false
                            }
                        }
                    }
                }

            }
        }
    }




    LaunchedEffect(key1 = fetchDataWord.value, block = {

        if (fetchDataWord.value) {
            soundUrl.value = ""

            when (dictionaryApi.intValue) {
                1 -> {
                    wordViewModel.getDataWordFromFreeDictionary(word.value)
                        .observe(lifecycleOwner) {

                            when (it) {
                                is NetworkResult.Error -> {
                                    fetchDataWord.value = false
                                }

                                is NetworkResult.Loading -> {
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
                                            soundUrl.value =
                                                it.data[0].phonetics?.first()?.audio.orEmpty()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

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


//                                        if (type.value == "verb") {
//                                            pastSimple.value =
//                                                verbList.first { it.word == word.value }.past_simple.orEmpty()
//                                            pastParticiple.value =
//                                                verbList.first { it.word == word.value }.pp.orEmpty()
//                                        }


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

                                    try {
                                        val audio =
                                            it.data?.first()?.headwordInformation?.pronunciations?.first()?.sound?.audio
                                        if (audio != null) {
                                            soundUrl.value =
                                                "https://media.merriam-webster.com/audio/prons/en/us/mp3/${
                                                    getSubdirectory(audio)
                                                }/$audio.mp3"
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
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
//                                                        try {
//                                                            (it as SenseItem).dt?.forEach {
//                                                                it.forEach {
//                                                                    timber("ajkaefhjeahfkjehkfjhew ${it.text}")
//                                                                }
//                                                            }
//                                                        }catch (e: Exception){
//                                                            e.printStackTrace()
//                                                        }
//
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }

//                                    if (type.value == "verb") {
//                                        pastSimple.value =
//                                            verbList.first { it.word == word.value }.past_simple.orEmpty()
//                                        pastParticiple.value =
//                                            verbList.first { it.word == word.value }.pp.orEmpty()
//                                    }
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
                                fetchDataWord.value = false
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

                                    soundUrl.value = it.data.data?.pronunciations?.first { it.accent == "am" }?.mp3.orEmpty()
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

    if (type.value == "verb" && pastSimple.value.isEmpty() && pastParticiple.value.isEmpty()) {
        wordViewModel.englishVerb(word.value).observeAsState(initial = null).value.let {
            pastSimple.value =
                it?.pastSimple.orEmpty()
            pastParticiple.value =
                it?.pp.orEmpty()
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
                    .fillMaxWidth(), text = word, label = stringResource(R.string.word),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.microphone_02),
                enabledLeadingIcon = true,
                leadingIconOnClick = {
                    openVoiceToSpeech = true
                    voiceForWord = true
                }
            ) {
                fillGermanNoun = true
            }

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


            if (similarWords.value.orEmpty().isNotEmpty()){
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .border(width = 2.dp, shape = RoundedCornerShape(8.dp), color = Warning300)
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.already_added),
                        style = fontMedium14(MaterialTheme.colorScheme.onSurface)
                    )

                    repeat(similarWords.value.size) { index ->

                        SampleItem(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            title = similarWords.value[index].word.orEmpty(),
                            secondaryText = similarWords.value[index].type.orEmpty(),
                            endString = getFormattedTimestamp(
                                similarWords.value[index].dateCreated.orDefault()),
                            id = similarWords.value[index].id,
                            onClick = {_, id, _ ->
                                navController.navigate(Screens.WordDetailScreen.name + "?wordId=$id")

                            })
                    }
                }
            }

            if (word.value.isNotEmpty() && type.value.isNotEmpty() &&
                language == "English"
            ) {

                Row(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
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
                        style = ButtonStyle.Contained,
                        iconStart = if (!isPurchased) painterResource(id = R.drawable.icons8_clock_1_1) else null
                    ) {

                        if (isPurchased) {
                            fetchDataWord.value = true
                            dictionaryApi.intValue = 2
                        } else {
                            descriptionType = DescriptionType.DICTIONARY
                            openDialog = true
                        }

                    }
                }

                if (isAllowThirdDictionary){
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        text = stringResource(id = R.string.auto_fill_from_third),
                        size = ButtonSize.LG,
                        type = ButtonType.Secondary,
                        style = ButtonStyle.Contained,
                        iconStart = if (!isPurchased) painterResource(id = R.drawable.icons8_clock_1_1) else null
                    ) {
                        if (isPurchased) {
                            fetchDataWord.value = true
                            dictionaryApi.intValue = 3
                        } else {
                            descriptionType = DescriptionType.DICTIONARY
                            openDialog = true
                        }
                    }
                }

            }


            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = translation,
                label = stringResource(R.string.translation)
            )


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


            } else if ((type.value == "noun" || type.value == "pronoun") && language == "German") {
                EditText(
                    Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    text = article,
                    label = stringResource(R.string.article),
                    readOnly = true,
                    onClick = {
                        openArticleBottomSheet = true
                    }
                )

//                EditText(
//                    Modifier
//                        .padding(top = 8.dp)
//                        .fillMaxWidth(),
//                    text = article,
//                    label = stringResource(R.string.grammatical_case),
//                    readOnly = true,
//                    onClick = {
//                        openCaseBottomSheet = true
//                    }
//                )

                EditText(
                    Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    text = plural,
                    label = stringResource(R.string.plural)
                )
            }


            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = pronunciation,
                label = stringResource(R.string.pronunciation),
                enabled = !fetchDataWord.value
            )
        }

//        items(1) {
//            Table()
//        }


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

            Row(modifier = Modifier.padding(top = 16.dp)) {
                ButtonIcon(modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                    icon = if (micStart) R.drawable.stop else if (audioFile?.exists()
                            .orFalse()
                    ) R.drawable.play else R.drawable.microphone,
                    subText = if (micStart) "recording" else null,
                    removeBtn = audioFile != null && !micStart && audioFile?.exists().orFalse(),
                    removeOnClick = {
                        audioFile?.delete()
                        audioFile = null
                    }) {
                    if (!allowRecording) {
                        //ask permission
                        micPermissionResultLauncher.launch(
                            Manifest.permission.RECORD_AUDIO
                        )
                    } else if (micStart) {
                        //stop recording
                        recorder!!.stop()
                        micStart = false
                    } else if (audioFile != null && audioFile?.exists().orFalse()) {
                        // play voice
                        if (player.isPlaying()) return@ButtonIcon
                        try {
                            player.playFile(audioFile ?: return@ButtonIcon)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        timber("voice-file-path ${audioFile?.absoluteFile}")
                    } else {
                        //recording
                        File(
                            context.getExternalFilesDir("backups/sounds"),
                            "${audioFileName}.WAV"
                        ).also {
                            recorder!!.start(it)
                            audioFile = it
                        }
                        micStart = true
                    }


                }
                ButtonIcon(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp), icon = R.drawable.speaker,
                    enable = soundUrl.value.isNotEmpty()
                ) {
                    val mp = MediaPlayer()
                    try {
                        scope.launch {
                            mp.setDataSource(soundUrl.value)
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    mp.prepare()
                                }catch (e: Exception){
                                    e.printStackTrace()
                                }
                            }
                            mp.setOnPreparedListener {
                                mp.start()
                            }
                        }
                    } catch (e: IOException) {
                        timber("ERR ::: ${e.printStackTrace()}")
                    }
                }
            }

            ButtonIcon(
                modifier = Modifier.padding(top = 16.dp),
                icon = R.drawable.image_rectangle,
                imagePath = imagePath
            ) {

                if (isPurchased) {
                    if (imagePath == null) {
                        mediaPicker
                            .launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                    } else {
                        File(imagePath!!).delete()
                        imagePath = null
                        imageUri = null
                        bitmap = null
                    }
                } else {
                    descriptionType = DescriptionType.IMAGE
                    openDialog = true
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

            if (listOfDefinitions.size > 3) {
                items(listOfDefinitions.subList(2, listOfDefinitions.size - 1)) {
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
                        timber("LIST_DATA ::: add :: $it")
                        if (it.trim().isNotEmpty()) {
                            exampleList.add(it)
                            example.value = ""
                        }

                    }
                },
                onChange = {
                    exampleField.invoke(it)
                }
            )
        }

        items(exampleList) {
            SampleItem(title = it) { title, _, _ ->
                exampleList.remove(title)
            }
        }

        item {
            DividerLine(
                modifier = Modifier.padding(top = 24.dp),
                title = stringResource(id = R.string.advance)
            )

            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(), text = synonym, label = stringResource(R.string.synonyms),
                supportText = stringResource(id = R.string.seperate_with_comma)

            )

            EditText(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(), text = antonyms, label = stringResource(R.string.antonyms),
                supportText = stringResource(id = R.string.seperate_with_comma)
            )
        }

        item {
            if (type.value == "verb" && language == "German") {

                ItemOption(text = stringResource(id = R.string.indicative)) {
                    navController.navigate(Screens.VerbsFormScreen.name + "?verb=${word.value}&form=indicative")
                }

                ItemOption(text = stringResource(id = R.string.conjunctive)) {
                    navController.navigate(Screens.VerbsFormScreen.name + "?verb=${word.value}&form=conjunctive")
                }

                ItemOption(text = stringResource(id = R.string.conditional)) {
                    navController.navigate(Screens.VerbsFormScreen.name + "?verb=${word.value}&form=conditional")
                }

                ItemOption(text = stringResource(id = R.string.imperaticve), divider = false) {
                    navController.navigate(Screens.VerbsFormScreen.name + "?verb=${word.value}&form=imperative")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.padding(24.dp))
        }

    }


    if (openDialog) {
        NeedProDialog(
            type = descriptionType,
            onClick = {
                if (email.orEmpty().isNotEmpty()) {
                    navController.navigate(Screens.PaywallScreen.name)
                } else {
                    navController.navigate(Screens.GoogleLoginScreen.name + "?skip=${false}")
                }
            }) {
            openDialog = false
        }
    }
}


private fun saveImageInFolder(bitmap: Bitmap?, context: Context, name: String): File {
    val directory = File(
        context.getExternalFilesDir("backups/images"),
        "${name}${System.currentTimeMillis()}.jpeg"
    )
    directory.parentFile?.mkdir()

    val fos: FileOutputStream?
    try {
        fos = FileOutputStream(directory)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//        bitmap?.compressBitmap(fos)
        fos.close()
    } catch (e: java.lang.Exception) {
        timber("SAVE IMAGE ERR :: ${e.message}")
    }

    return directory
}

