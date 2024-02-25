package com.ntg.vocabs.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.EditText
import com.ntg.vocabs.components.ItemSelectable
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.Failure
import com.ntg.vocabs.model.Success
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.db.VocabItemList
import com.ntg.vocabs.model.then
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.util.notEmptyOrNull
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLanguageScreen(navController: NavController, wordViewModel: WordViewModel, listId: Int?) {

    timber("akwjdklawjdlkwjadlkjawlkdjw $listId")
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val submitList = remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            val listData = wordViewModel.findList(listId)?.observeAsState()
            Content(paddingValues = innerPadding, navController, wordViewModel, listData?.value, submitList)
        },
        bottomBar = {
            BottomBar(submitList, listId != -1)
        }
    )


}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    wordViewModel: WordViewModel,
    listForEdit: VocabItemList?,
    submitList: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    val list = listOf(
        "English",
        "German"
    )

    val ctx = LocalContext.current
    var language by remember {
        mutableStateOf("English")
    }
    val name = remember {
        mutableStateOf("")
    }
    val anotherLanguage = remember {
        mutableStateOf("")
    }

    var isApplied by remember {
        mutableStateOf(false)
    }


    if (listForEdit != null && !isApplied) {
        language = listForEdit.language
        name.value = listForEdit.title
        if (listForEdit.language !in list) anotherLanguage.value = listForEdit.language
        isApplied = true
    }


    val isExist = wordViewModel.isListExist(
        name = name.value,
        language = language
    ).observeAsState().value != 0

    if (submitList.value) {

        val result = notEmptyOrNull(name.value, stringResource(id = R.string.choose_name_for_list))
            .then { notEmptyOrNull(language, ctx.getString(R.string.select_a_language)) }

        if (result is Success){
            if (listForEdit != null) {
                listForEdit.language = language
                listForEdit.title = name.value
                wordViewModel.updateVocabList(
                    listForEdit
                )
                navController.popBackStack()

            } else if (isExist) {
                ctx.toast(ctx.getString(R.string.this_list_already_exist))
            } else {
                wordViewModel.addNewVocabList(
                    VocabItemList(
                        0,
                        title = name.value,
                        language = language,
                        isSelected = false
                    )
                ){
                    wordViewModel.selectList(it.toInt())
                    navController.popBackStack()
                }
//                if (language == "German"){
//                    navController.navigate(Screens.DownloadScreen.name + "?enableBottomBar=${true}",
//                        NavOptions.Builder()
//                            .setPopUpTo(Screens.SelectLanguageScreen.name, inclusive = true)
//                            .build()
//                    )
//                }else{
//                }
//                navController.popBackStack()

            }
        }else if (result is Failure){
            ctx.toast(result.errorMessage)
        }
        submitList.value = false


    }




    LazyColumn(modifier = Modifier.padding(horizontal = 32.dp)) {

        item {
            TypewriterText(
                modifier = Modifier.padding(top = 64.dp, bottom = 32.dp),
                texts = listOf(stringResource(id = if (listForEdit == null) R.string.create_new_list else R.string.edit_your_list)),
                singleText = true,
                speedType = 20L
            )
        }

        item {
            Column {
                Text(
                    text = stringResource(id = R.string.enter_list_name),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )

                EditText(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(), label = stringResource(id = R.string.name),
                    text = name
                )
            }
        }

        item {
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                text = stringResource(id = R.string.witch_language),
                style = fontMedium14(MaterialTheme.colorScheme.onSurface)
            )
        }

        items(list) {

            ItemSelectable(
                modifier = Modifier.padding(top = 8.dp),
                text = it,
                isSelected = language == it
            ) {
                language = it
            }

        }

        item {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 100.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = if (language in list) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (anotherLanguage.value.isNotEmpty()) {
                        language = anotherLanguage.value
                    }
                }
            ) {

                Text(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(start = 24.dp), text = "other", style = fontMedium14(
                        MaterialTheme.colorScheme.onBackground
                    )
                )

                EditText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 16.dp),
                    label = stringResource(id = R.string.language),
                    text = anotherLanguage
                ) {
                    language = it
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    submitList: MutableState<Boolean> = remember { mutableStateOf(false) },
    isEdit: Boolean
){
    Column(modifier = Modifier.padding(horizontal = 32.dp).background(MaterialTheme.colorScheme.background)) {
        Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)

        CustomButton(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            text = if (isEdit) stringResource(id = R.string.edit) else stringResource(id = R.string.save),
            size = ButtonSize.XL
        ) {
            submitList.value = true
        }
    }

}
