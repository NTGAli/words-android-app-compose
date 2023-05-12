package com.ntg.mywords.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.EditText
import com.ntg.mywords.components.SampleItem
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
fun AddEditWordScreen(navController: NavController, wordViewModel: WordViewModel) {
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

            Content(paddingValues = innerPadding){
                timber("WORD_DATA", it.toString())
                wordData = it
            }

        }
        , bottomBar = {

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
                ){


                    val result = notEmptyOrNull(wordData.word, context.getString(R.string.err_word_required))
                        .then { notEmptyOrNull(wordData.type, context.getString(R.string.err_type_required)) }
                        .then { notEmptyOrNull(wordData.example.toString(), context.getString(R.string.err_example_required)) }
                        .then { notFalse(!wordViewModel.checkIfExist(wordData.word.orEmpty(), wordData.type.orEmpty()), context.getString(R.string.err_word_already_exist)) }

                    when(result){

                        is Success -> {
                            wordViewModel.addNewWord(wordData)
                        }

                        is Failure -> {
                            context.toast(result.errorMessage)
                        }

                    }


                }

            }

        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(paddingValues: PaddingValues, wordData:(Word) -> Unit) {

    val word = remember {
        mutableStateOf("")
    }
    val pronunciation = remember {
        mutableStateOf("")
    }
    val type = remember {
        mutableStateOf("")
    }
    val definition = remember {
        mutableStateOf("")
    }
    val example = remember {
        mutableStateOf("")
    }

    val exampleList = remember {
        mutableStateListOf<String>()
    }


    wordData(
        Word(
            0,
            word = word.value,
            type = type.value,
            pronunciation = pronunciation.value,
            definition = definition.value,
            example = exampleList,
            dateCreated = System.currentTimeMillis()
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
                    SampleItem(title = it) {
                        type.value = it
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
                    if (it.isNotEmpty()){
                        exampleList.add(it)
                        example.value = ""
                    }
                }
            )

        }

        items(exampleList){
            timber("LIST_DATA", "$exampleList")
            SampleItem(title = it, painter = painterResource(id = R.drawable.chart_full)){
                exampleList.remove(it)
            }
        }

    }


}