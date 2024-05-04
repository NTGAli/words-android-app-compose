package com.ntg.vocabs.screens

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.DefinitionItem
import com.ntg.vocabs.components.DescriptionType
import com.ntg.vocabs.components.LoadingView
import com.ntg.vocabs.components.NeedProDialog
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.TextWithContext
import com.ntg.vocabs.model.db.EnglishVerbs
import com.ntg.vocabs.model.db.VerbForms
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.Primary500
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontMedium24
import com.ntg.vocabs.util.getSubdirectory
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orTrue
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.toPronunciation
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

private val listOfDictionary = listOf(
    "Dictionary number one",
    "Dictionary number two",
//    "Dictionary number three",
)

data class DefData(
    val def: String,
    val example: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineWordDetailsScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel,
    word: String,
    type: String
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    var onlineWord by remember {
        mutableStateOf<Word?>(null)
    }

    var openBottomSheet by remember {
        mutableStateOf(false)
    }

    var openDialog by remember {
        mutableStateOf(false)
    }

    var selectedDictionary by remember {
        mutableStateOf(listOfDictionary.first())
    }

    val isPurchased =
        loginViewModel.getUserData().collectAsState(initial = null).value?.isPurchased.orFalse()

    val email = loginViewModel.getUserData().collectAsState(initial = null).value?.email

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = word,
                enableNavigation = true,
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() },
//                buttonText = stringResource(id = R.string.add_to_words),
                popupItems = listOf(
//                    PopupItem(
//                        id = 1,
//                        title = "edit",
//                        icon = painterResource(id = R.drawable.menu_16_2)
//                    )
                ),
                popupItemOnClick = {
                    openBottomSheet = true
                },

                )
        },
        content = { innerPadding ->
            Content(wordViewModel, innerPadding, word, type, selectedDictionary) {
                onlineWord = it
            }
        },
        bottomBar = {
            val isExist = wordViewModel.findWord(word, type)
                ?.observeAsState()?.value?.any { it.definition.orEmpty() == onlineWord?.definition.orEmpty() }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                Divider(
                    Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                CustomButton(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.add_to_words),
                    size = ButtonSize.XL
                ) {
                    if (onlineWord?.definition.orEmpty().isEmpty()) {
                        context.toast(context.getString(R.string.select_at_least_definition))
                    } else if (isExist.orTrue()) {
                        context.toast(context.getString(R.string.err_word_already_exist))
                    } else if (onlineWord != null) {
                        wordViewModel.addNewWord(onlineWord!!)
                        navController.popBackStack()
                    }
                }

            }

        }
    )


    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
        ) {

            Column(
                Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                LazyColumn(content = {

                    item {
                        Text(
                            modifier = Modifier.padding(bottom = 24.dp),
                            text = stringResource(R.string.select_a_dictionary_to_load_data),
                            style = fontMedium14(
                                MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }

                    itemsIndexed(listOfDictionary) {index, it ->
                        SampleItem(
                            title = it,
                            enableRadioButton = true,
                            radioSelect = mutableStateOf(selectedDictionary == it),
                            onClick = { text, _, isSelect ->
                                if (isPurchased || index == 0){
                                    selectedDictionary = text
                                    openBottomSheet = false
                                }else{
                                    openDialog = true
                                }
                            })
                    }

                    item {
                        Spacer(modifier = Modifier.padding(24.dp))
                    }
                })

            }


        }
    }

    if (openDialog){
        NeedProDialog(type = DescriptionType.DICTIONARY, onClick = {
            if (email.orEmpty().isNotEmpty()){
                navController.navigate(Screens.PaywallScreen.name)
            }else{
                navController.navigate(Screens.GoogleLoginScreen.name + "?skip=${false}")
            }
        }) {
            openDialog = false
        }
    }
}

@Composable
private fun Content(
    wordViewModel: WordViewModel,
    paddingValues: PaddingValues,
    word: String,
    type: String,
    selectedDictionary: String,
    insert: (Word) -> Unit
) {

    var visible by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val listId = wordViewModel.currentList().observeAsState().value?.id

    var pronunciation by remember {
        mutableStateOf("")
    }

    var soundUrl by remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    val definitions = remember {
        mutableStateListOf<DefData>()
    }

    var defSelected by remember {
        mutableStateOf<DefData?>(null)
    }

    var verbForms by remember {
        mutableStateOf<EnglishVerbs?>(null)
    }

    if (type == "verb") {
        verbForms = wordViewModel.englishVerb(word).observeAsState().value
    }

    val scope = rememberCoroutineScope()

    if (listId != null) {
        insert.invoke(
            Word(
                id = 0,
                word = word,
                type = type,
                listId = listId,
                verbForms = try {
                    VerbForms(
                        pastSimple = verbForms?.pastSimple,
                        pastParticiple = verbForms?.pp
                    )
                } catch (e: Exception) {
                    null
                },
                pronunciation = pronunciation,
                definition = defSelected?.def,
                sound = soundUrl,
                example = defSelected?.example.orEmpty(),
                dateCreated = System.currentTimeMillis(),
                lastRevisionTime = System.currentTimeMillis()
            )
        )
    }

    LaunchedEffect(key1 = selectedDictionary, block = {

        when (selectedDictionary) {

            listOfDictionary[0] -> {
                wordViewModel.getDataWordFromFreeDictionary(word)
                    .observe(lifecycleOwner) {

                        when (it) {
                            is NetworkResult.Error -> {
                                timber("getDataWordFromFreeDictionary ::: ER ${it.message}")
                            }

                            is NetworkResult.Loading -> {
                                timber("getDataWordFromFreeDictionary ::: LD")
                                loading = true
                                defSelected = null
                            }

                            is NetworkResult.Success -> {
                                timber("getDataWordFromFreeDictionary ::: SC ::: ${it.data}")
                                definitions.clear()
                                loading = false

                                if (it.data != null) {
                                    timber("WORD_DATA_VOCAB :: ${it.data}")
                                    pronunciation = it.data[0].phonetic.orEmpty()

                                    try {
                                        soundUrl =
                                            it.data[0].phonetics?.first()?.audio.orEmpty()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    try {
                                        it.data.forEach {
                                            it.meanings?.filter { it.partOfSpeech == type }
                                                ?.forEach {
                                                    it.definitions.orEmpty().forEach {
                                                        definitions.add(
                                                            DefData(
                                                                it.definition.orEmpty(),
                                                                listOf(it.example.orEmpty())
                                                            )
                                                        )
                                                    }

                                                }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                } else {
                                    context.toast(context.getString(R.string.not_exist))
                                }
                            }
                        }

                    }
            }

            listOfDictionary[1] -> {
                wordViewModel.getDataWord(word).observe(lifecycleOwner) {
                    when (it) {
                        is NetworkResult.Error -> {
                            timber("WORD_DATA :: ERR ${it.message}")
                            context.toast(context.getString(R.string.error_occurred))
                        }

                        is NetworkResult.Loading -> {
                            timber("WORD_DATA ::  LD")
                            loading = true
                            defSelected = null
                        }

                        is NetworkResult.Success -> {
                            timber("WORD_DATA :: ${it.data}")
                            loading = false
                            if (it.data.orEmpty().isNotEmpty()) {
                                definitions.clear()
//                                wordDataItems = it.data.orEmpty()
                                pronunciation =
                                    it.data?.get(0)?.headwordInformation?.pronunciations?.get(
                                        0
                                    )?.mw.orEmpty().toPronunciation()

                                it.data?.filter { it.functionalLabel == type }
                                    ?.forEach {
                                        it.shortDefinitions?.forEach { def ->
                                            definitions.add(DefData(def))
                                        }
                                    }

                                try {
                                    val audio =
                                        it.data?.first()?.headwordInformation?.pronunciations?.first()?.sound?.audio
                                    if (audio != null) {
                                        soundUrl =
                                            "https://media.merriam-webster.com/audio/prons/en/us/mp3/${
                                                getSubdirectory(audio)
                                            }/$audio.mp3"
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                context.toast(context.getString(R.string.not_exist))
                            }
                        }
                    }

                }
            }

            listOfDictionary[2] -> {
                wordViewModel.getWord(word, type).observe(lifecycleOwner) {
                    when (it) {
                        is NetworkResult.Error -> {

                        }

                        is NetworkResult.Loading -> {
                            loading = true
                            defSelected = null
                        }

                        is NetworkResult.Success -> {
                            loading = false
                            if (it.data != null) {
                                timber("WORD_DATA_VOCAB :: ${it.data}")
                                definitions.clear()
                                pronunciation =
                                    it.data.data?.pronunciations?.first { it.accent == "am" }?.pronunciation.orEmpty()


                                it.data.data?.definitions?.sortedBy { it.number }?.forEach {
                                    definitions.add(
                                        DefData(
                                            it.definition.orEmpty(),
                                            it.examples.orEmpty()
                                        )
                                    )
                                }

                                soundUrl =
                                    it.data.data?.pronunciations?.first { it.pronunciation == "am" }?.mp3.orEmpty()

                            } else {
                                context.toast(context.getString(R.string.not_exist))
                            }
                        }
                    }
                }
            }

        }


    })


    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
    ) {

        item {
            Row(
                modifier = Modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word,
                    style = fontMedium24(MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = type,
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        item {
            if (verbForms?.pastSimple.orEmpty().isNotEmpty() &&
                verbForms?.pp.orEmpty().isNotEmpty()
            ) {

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            visible = !visible
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(start = 8.dp),
                        text = stringResource(id = R.string.verb_forms),
                        style = fontMedium14(
                            MaterialTheme.colorScheme.onSurface
                        )
                    )

                    AnimatedVisibility(visible = visible) {

                        Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                            if (verbForms?.pastSimple != null) {
                                TextWithContext(
                                    title = stringResource(id = R.string.past_simple),
                                    description = verbForms?.pastSimple.orEmpty()
                                )
                            }

                            if (verbForms?.pp != null) {
                                TextWithContext(
                                    modifier = Modifier.padding(top = 8.dp),
                                    title = stringResource(id = R.string.past_participle),
                                    description = verbForms?.pp.orEmpty()
                                )
                            }
                        }

                    }

                }
            }
        }

        if (soundUrl.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val mp = MediaPlayer()
                        try {
                            scope.launch {
                                mp.setDataSource(soundUrl)
                                try {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        mp.prepare()
                                    }
                                }catch (e: Exception){
                                    e.printStackTrace()
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
                            tint = Primary500
                        )
                    }
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = pronunciation,
                        style = fontMedium14(
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.padding(16.dp))
        }

        items(definitions.toList()) {

            DefinitionItem(
                modifier = Modifier.padding(top = 8.dp),
                definition = it.def.orEmpty(),
                example = it.example,
                isSelected = defSelected == it
            ) {
                defSelected = it
            }

        }

        item {
            Spacer(modifier = Modifier.padding(24.dp))
        }

    }

    if (loading){
        LoadingView()
    }
}