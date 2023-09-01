package com.ntg.mywords.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.ItemList
import com.ntg.mywords.components.TypewriterText
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.model.db.VocabItemList
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.fontRegular12
import com.ntg.mywords.util.timber
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListScreen(navController: NavController, wordViewModel: WordViewModel, loginViewModel: LoginViewModel) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navController, wordViewModel, loginViewModel)
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController, wordViewModel: WordViewModel, loginViewModel: LoginViewModel) {

    val list = remember {
        mutableStateOf(listOf<VocabItemList>())
    }

    var username by remember {
        mutableStateOf("")
    }

    val title = remember {
        mutableStateOf(listOf<String>())
    }

    list.value = wordViewModel.getAllVocabList().observeAsState().value ?: listOf()
    username = loginViewModel.getUserData().asLiveData().observeAsState().value?.name.orEmpty()


    if (list.value.isEmpty()){
        title.value = listOf(stringResource(id = R.string.no_list_message, username))
    }else{
        title.value = listOf(stringResource(id = R.string.select_a_list, username))
    }



    Column(modifier = Modifier.padding(horizontal = 32.dp)) {

        TypewriterText(
            modifier = Modifier.padding(top = 64.dp),
            texts = title.value,
            singleText = true,
            speedType = 10L
        )

        if (list.value.isNotEmpty()){
            Text(modifier = Modifier.padding(top = 4.dp), text = stringResource(id = R.string.switchable_list), style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))
        }else{

            LazyColumn{
                items(list.value){

                    ItemList(
                        title = it.title,
                        subTitle = it.language,
                        isSelected = true,
                        onClick = {}
                    )

                }
            }

        }




        CustomButton(
            modifier = Modifier.padding(top = 32.dp),
            text = stringResource(id = R.string.add_new),
            style = ButtonStyle.TextOnly,
            type = ButtonType.Primary
        ) {
            navController.navigate(Screens.SelectLanguageScreen.name)
        }
    }

}