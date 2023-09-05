package com.ntg.mywords.screens.login

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
import com.ntg.mywords.R
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.EditText
import com.ntg.mywords.components.ItemSelectable
import com.ntg.mywords.components.TypewriterText
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.model.db.VocabItemList
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.util.toast
import com.ntg.mywords.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLanguageScreen(navController: NavController, wordViewModel: WordViewModel) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navController, wordViewModel)
        }
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    wordViewModel: WordViewModel
) {
    val list = listOf(
        "english",
        "franch",
        "adawdwa",
        "wdwadwad"
    )

    val ctx = LocalContext.current
    var language by remember {
        mutableStateOf("english")
    }
    val name = remember {
        mutableStateOf("")
    }
    val anotherLanguage = remember {
        mutableStateOf("")
    }

    val isExist = wordViewModel.isListExist(
        name = name.value,
        language = language
    ).observeAsState().value != 0





    LazyColumn(modifier = Modifier.padding(horizontal = 32.dp)) {

        item {
            TypewriterText(
                modifier = Modifier.padding(top = 64.dp, bottom = 32.dp),
                texts = listOf(stringResource(id = R.string.create_new_list)),
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
                .padding(top = 8.dp)
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
                        .padding(horizontal = 8.dp), label = stringResource(id = R.string.language), text = anotherLanguage
                ) {
                    language = it
                }
            }
        }

        item {
            CustomButton(
                modifier = Modifier.padding(top = 24.dp)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.save),
                enable = language.isNotEmpty() && name.value.isNotEmpty(),
                size = ButtonSize.XL
            ) {

                if (isExist){
                    ctx.toast(ctx.getString(R.string.this_list_already_exist))
                }else{

                    wordViewModel.addNewVocabList(
                        VocabItemList(
                            0,
                            title = name.value,
                            language = language,
                            isSelected = false
                        )
                    )
                    navController.popBackStack()

                }
            }
        }
    }


}
