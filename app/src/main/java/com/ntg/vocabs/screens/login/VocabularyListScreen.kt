package com.ntg.vocabs.screens.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.ntg.vocabs.R
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.ItemList
import com.ntg.vocabs.components.Message
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.model.db.VocabItemList
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.screens.getGoogleSignInClient
import com.ntg.vocabs.screens.setting.ReadBackupFromStorage
import com.ntg.vocabs.screens.setting.RestoreUserDataFromServer
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontRegular12
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.util.unixTimeToReadable
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel,
    backupViewModel: BackupViewModel
) {

    var openBottomSheet by remember {
        mutableStateOf(false)
    }

    var loading by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navController, wordViewModel, loginViewModel)
        },
        bottomBar = {
            CustomButton(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.have_backup),
                type = ButtonType.Primary,
                style = ButtonStyle.TextOnly,
                size = ButtonSize.LG,
            ) {
                openBottomSheet = true

            }
        }
    )

    ReadBackupFromStorage(launch = openBottomSheet, isLaunched = {openBottomSheet = false}) {
        if (it.orEmpty().isNotEmpty()) {
            backupViewModel.importToDB(it!!) { isSucceed ->
                if (isSucceed) {
                    context.toast(R.string.backup_imported)
                    navController.navigate(Screens.VocabularyListScreen.name)
                } else {
                    context.toast(R.string.file_not_supported)
                }
                loading = false

            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
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
    var deleteList by remember {
        mutableStateOf(false)
    }
    val title = remember {
        mutableStateOf(listOf<String>())
    }
    val listId = remember {
        mutableStateOf(-1)
    }

    var openBottomSheet by remember {
        mutableStateOf(false)
    }


    list.value = wordViewModel.getAllVocabList().observeAsState().value ?: listOf()
//    email = loginViewModel.getUserData().asLiveData().observeAsState().value?.email.orEmpty()


    if (restoreData) {
        restoreData = false
        RestoreUserDataFromServer(email,wordViewModel) {
            loading = false
            if (it) ctx.toast(ctx.getString(R.string.restore_done))
            else ctx.toast(ctx.getString(R.string.restore_failed))
        }
    }

    if (deleteList) {
        wordViewModel.deleteListById(listId.value)
        wordViewModel.deleteWordsOfList(listId.value)
        wordViewModel.deleteTimeSpentOfList(listId.value)
        deleteList = false
    }

    if (list.value.isEmpty()) {
        if (username == "no one"){
            title.value = listOf(stringResource(id = R.string.no_list_message, ""))
        }else{
            title.value = listOf(stringResource(id = R.string.no_list_message, username))
        }
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
                        modifier = Modifier.padding(top = 8.dp),
                        id = it.id,
                        title = it.title,
                        subTitle = it.language,
                        isSelected = true,
                        onClick = { id ->
                            wordViewModel.selectList(id)
                            navController.navigate(Screens.HomeScreen.name) {
                                popUpTo(0)
                            }
                        },
                        editCallback = { lId ->
                            navController.navigate(Screens.SelectLanguageScreen.name + "?listId=$lId")
                        },
                        deleteCallback = { lId ->
                            listId.value = lId
                            openBottomSheet = true
                        }
                    )

                }
            }
        }


        CustomButton(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.add_new),
            style = ButtonStyle.TextOnly,
            type = ButtonType.Primary,
            size = ButtonSize.XL
        ) {
            navController.navigate(Screens.SelectLanguageScreen.name)
        }
    }


    if (openBottomSheet) {
        ModalBottomSheet(onDismissRequest = { openBottomSheet = false }) {

            Column(
                Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.title_delete_list), style = fontMedium14(
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResource(id = R.string.desc_delete_list),
                    style = fontMedium12(
                        MaterialTheme.colorScheme.outline
                    )
                )

                Row(modifier = Modifier.padding(top = 16.dp)) {
                    CustomButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        text = stringResource(id = R.string.no),
                        type = ButtonType.Secondary,
                        style = ButtonStyle.Outline,
                        size = ButtonSize.LG
                    ) {
                        openBottomSheet = false
                    }
                    CustomButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        text = stringResource(id = R.string.yes),
                        type = ButtonType.Danger,
                        size = ButtonSize.LG
                    ) {
                        deleteList = true
                        openBottomSheet = false
                    }
                }
            }


        }
    }

}