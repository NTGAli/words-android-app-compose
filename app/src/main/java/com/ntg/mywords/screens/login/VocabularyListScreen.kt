package com.ntg.mywords.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.ItemList
import com.ntg.mywords.components.Message
import com.ntg.mywords.components.TypewriterText
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.model.db.VocabItemList
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.screens.setting.RestoreUserDataFromServer
import com.ntg.mywords.ui.theme.fontRegular12
import com.ntg.mywords.util.toast
import com.ntg.mywords.util.unixTimeToReadable
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navController, wordViewModel, loginViewModel)
        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel
) {

    val ctx = LocalContext.current
    val list = remember {
        mutableStateOf(listOf<VocabItemList>())
    }
    var username by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var backupTime by remember {
        mutableStateOf("")
    }
    var restoreData by remember {
        mutableStateOf(false)
    }
    var loading by remember {
        mutableStateOf(false)
    }
    val title = remember {
        mutableStateOf(listOf<String>())
    }


    list.value = wordViewModel.getAllVocabList().observeAsState().value ?: listOf()
    username = loginViewModel.getUserData().asLiveData().observeAsState().value?.name.orEmpty()
    email = loginViewModel.getUserData().asLiveData().observeAsState().value?.email.orEmpty()
    wordViewModel.lastUserBackup(email).observe(LocalLifecycleOwner.current) {
        when (it) {
            is NetworkResult.Error -> {
            }
            is NetworkResult.Loading -> {
            }
            is NetworkResult.Success -> {
                if (it.data != "NO_BACKUP_FOUND") {
                    backupTime = it.data.orEmpty()
                }
            }
        }
    }

    if (restoreData) {
        restoreData = false
        RestoreUserDataFromServer(wordViewModel) {
            loading = false
            if (it) ctx.toast(ctx.getString(R.string.restore_done))
            else ctx.toast(ctx.getString(R.string.restore_failed))
        }
    }


    if (list.value.isEmpty()) {
        title.value = listOf(stringResource(id = R.string.no_list_message, username))
    } else {
        title.value = listOf(stringResource(id = R.string.select_a_list, username))
    }



    Column(modifier = Modifier.padding(horizontal = 32.dp)) {


        if (backupTime.isNotEmpty() && list.value.isEmpty()) {
            Message(
                modifier = Modifier.padding(top = 24.dp),
                icon = painterResource(id = R.drawable.download),
                title = "your backup is available",
                subTitle = "last backup: ${backupTime.toLong().unixTimeToReadable()}",
                btnText = "restore",
                btnLoading = loading
            ) {
                restoreData = true
                loading = true
            }
        }
        TypewriterText(
            modifier = Modifier.padding(top = 32.dp),
            texts = title.value,
            singleText = true,
            speedType = 10L
        )

        if (list.value.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
                text = stringResource(id = R.string.switchable_list),
                style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
            )

            LazyColumn {
                items(list.value) {

                    ItemList(
                        modifier = Modifier.padding(vertical = 8.dp),
                        id = it.id,
                        title = it.title,
                        subTitle = it.language,
                        isSelected = true,
                        onClick = { id ->
                            wordViewModel.selectList(id)
                            navController.navigate(Screens.HomeScreen.name) {
                                popUpTo(0)
                            }
                        }
                    )

                }
            }
        }


        CustomButton(
            modifier = Modifier.padding(top = 24.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.add_new),
            style = ButtonStyle.TextOnly,
            type = ButtonType.Primary
        ) {
            navController.navigate(Screens.SelectLanguageScreen.name)
        }
    }

}