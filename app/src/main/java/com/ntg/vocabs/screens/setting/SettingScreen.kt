package com.ntg.vocabs.screens.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.core.content.FileProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.vocabs.BuildConfig
import com.ntg.vocabs.R
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.ItemOption
import com.ntg.vocabs.model.req.BackupUserData
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.screens.login.logoutBottomSheet
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.util.*
import com.ntg.vocabs.util.Constant.Backup.BACKUP_FILE_NAME_IN_DIRECTORY
import com.ntg.vocabs.util.Constant.Backup.BACKUP_FOLDER_NAME
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.channels.FileChannel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    wordViewModel: WordViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.settings),
                scrollBehavior = scrollBehavior,
                navigationOnClick = {
                    navController.popBackStack()
                }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, navController, loginViewModel, wordViewModel)

        }
    )


}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    loginViewModel: LoginViewModel,
    wordViewModel: WordViewModel
) {

    val ctx = LocalContext.current
    val store = UserStore(LocalContext.current)
    val openBackupDialog = remember { mutableStateOf(false) }
    val openRestoreDialog = remember { mutableStateOf(false) }
    val backupOnDirecotry = remember {
        mutableStateOf(false)
    }
    val restoreFromServer = remember {
        mutableStateOf(false)
    }
    val visibleSuccess = remember {
        mutableStateOf(false)
    }
    val share = remember {
        mutableStateOf(false)
    }
    val import = remember {
        mutableStateOf(false)
    }
    val isUserLogged = remember {
        mutableStateOf(false)
    }

    val userEmail = remember {
        mutableStateOf("")
    }

    val openBottomSheet = remember {
        mutableStateOf(false)
    }


    isUserLogged.value =
        loginViewModel.getUserData().asLiveData().observeAsState().value?.email.orEmpty() != ""

    userEmail.value =
        loginViewModel.getUserData().asLiveData().observeAsState().value?.email.orEmpty()

    val theme =
        store.getTheme.collectAsState(initial = stringResource(id = R.string.system_default))
    val backupOption =
        loginViewModel.getUserData().asLiveData().observeAsState(null).value?.backupOption.orEmpty()
    val userWords = wordViewModel.getSizeOfWords().observeAsState(initial = -1).value


    if (openBottomSheet.value) {
        logoutBottomSheet(openBottomSheet) {
            wordViewModel.clearWordsTable()
            wordViewModel.clearVocabListsTable()
            wordViewModel.clearTimesTable()
            loginViewModel.clearUserData()
            openBottomSheet.value = false
            navController.navigate(Screens.InsertEmailScreen.name) {
                popUpTo(0)
            }
        }

    }


    if (restoreFromServer.value) {
        RestoreUserDataFromServer(wordViewModel = wordViewModel, email = userEmail.value) {
            restoreFromServer.value = false
            if (it) {
                visibleSuccess.value = true
            }
        }
    }

    if (visibleSuccess.value) {
        LaunchedEffect(
            visibleSuccess
        ) {
            delay(2000)
            visibleSuccess.value = false
        }
    }

    ShareUserBackup(share, wordViewModel = wordViewModel) {
        share.value = false
        openBackupDialog.value = false
    }




    ReadBackupFromStorage(launch = import.value) {
        if (it.orEmpty().isNotEmpty()) {
            wordViewModel.importToDB(it!!) { isSucceed ->
                if (isSucceed) {
                    ctx.toast(ctx.getString(R.string.backup_imported))
                } else {
                    ctx.toast(ctx.getString(R.string.file_not_supported))
                }
            }
        }
        openRestoreDialog.value = false
    }

    if (import.value) {
        import.value = false
    }







    LazyColumn(modifier = Modifier.padding(paddingValues)) {

        item {
            SettingTitle(title = stringResource(id = R.string.backup_and_restore))
            ItemOption(text = stringResource(id = R.string.backup)) {
                if (userWords > 0){
                    openBackupDialog.value = true
                }else{
                    ctx.toast(R.string.didnt_add_word)
                }
            }

            ItemOption(text = stringResource(id = R.string.restore), divider = false) {
                openRestoreDialog.value = true
            }

            SettingTitle(title = stringResource(id = R.string.account))

            if (isUserLogged.value.orFalse()) {
                ItemOption(text = stringResource(id = R.string.name)) {
                    navController.navigate(Screens.NameScreen.name)
                }
//                ItemOption(text = stringResource(id = R.string.email)) {
//                    navController.navigate(Screens.UpdateEmailScreen.name)
//                }
//                ItemOption(text = stringResource(id = R.string.change_password)) {
//
//                }
                ItemOption(text = stringResource(id = R.string.delete_account), divider = false) {
                    navController.navigate(Screens.DeleteAccountScreen.name)
                }
            } else {
                ItemOption(text = stringResource(id = R.string.login), divider = false) {
                    navController.navigate(Screens.InsertEmailScreen.name + "?skip=${false}")
                }
            }



            SettingTitle(title = stringResource(id = R.string.theme))
            ItemOption(
                text = theme.value.ifEmpty { stringResource(id = R.string.system_default) },
                divider = false
            ) {
                navController.navigate(Screens.ThemeScreen.name)
            }

            SettingTitle(title = stringResource(id = R.string.support_us))
            ItemOption(text = stringResource(id = R.string.share_vocab)) {

            }
            ItemOption(text = stringResource(id = R.string.leave_us_review), divider = false) {

            }

            SettingTitle(title = stringResource(id = R.string.other))
            ItemOption(text = stringResource(id = R.string.sign_out), divider = false) {
                openBottomSheet.value = true
            }
        }

    }




    if (openBackupDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 16.dp),
            onDismissRequest = {
                openBackupDialog.value = false
            },
            icon = {},
            title = {
                Text(text = stringResource(id = R.string.backup))
            },
            text = {
                Column {
                    ItemOption(
                        text = stringResource(id = R.string.backup_on_storage),
                        endIcon = painterResource(id = R.drawable.ok),
                        visibleWithAnimation = backupOnDirecotry,
                    ) {
                        if (!backupOnDirecotry.value) {
                            val backupFile =
                                File(ctx.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}")
                            saveFileToCustomDirectory(backupFile)
                            backupOnDirecotry.value = true
                            ctx.toast(
                                ctx.getString(
                                    R.string.backupped_success_on_document,
                                    BACKUP_FOLDER_NAME
                                )
                            )
                        }

                    }
                    ItemOption(text = stringResource(id = R.string.share)) {
                        share.value = true
                    }

                    ItemOption(text = stringResource(id = R.string.backup_on_google_drive), divider = false) {
                        if (backupOption.isEmpty() || backupOption == "Never" || backupOption == "Only when i tap ‘backup’") {
                            navController.navigate(Screens.AskBackupScreen.name)
                        } else {
                            navController.navigate(Screens.BackupScreen.name)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        openBackupDialog.value = false
                    }
                ) {
                    Text(stringResource(id = R.string.dismiss))
                }
            }
        )
    }

    if (openRestoreDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 16.dp),
            onDismissRequest = {
                openRestoreDialog.value = false
            },
            icon = {},
            title = {
                Text(text = stringResource(id = R.string.restore))
            },
            text = {
                Column {

                    Text(text = stringResource(id = R.string.attention_for_restore_message))

                    ItemOption(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(id = R.string.restore_from_drive),
                    ) {
                        if (backupOption.isEmpty() || backupOption == "Never" || backupOption == "Only when i tap ‘backup’") {
                            navController.navigate(Screens.AskBackupScreen.name)
                        } else {
                            navController.navigate(Screens.BackupScreen.name)
                        }
                    }
                    ItemOption(text = stringResource(id = R.string.str_import), divider = false) {
                        import.value = true
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        openRestoreDialog.value = false
                    }
                ) {
                    Text(stringResource(id = R.string.dismiss))
                }
            }
        )
    }


}

@Composable
private fun SettingTitle(title: String) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
        text = title,
        style = fontMedium14(MaterialTheme.colorScheme.primary)
    )
}


@Composable
private fun BackupOnServer(
    setBackup: MutableState<Boolean> = remember { mutableStateOf(false) },
    email: String,
    wordViewModel: WordViewModel,
    resultCallback: (Boolean) -> Unit
) {
    timber("SET_BACKUP_STATUS ${setBackup.value}")
    val owner = LocalLifecycleOwner.current
    UserBackup(wordViewModel = wordViewModel) { wordData ->
        if (setBackup.value) {
            wordViewModel.upload(wordData, email).observe(owner) {
                when (it) {
                    is NetworkResult.Error -> {
                        timber("BackupUserData :::: ERR ${it.message}")
                        resultCallback.invoke(false)
                    }

                    is NetworkResult.Loading -> {
                        timber("BackupUserData :::: LD")
                    }

                    is NetworkResult.Success -> {
                        timber("BackupUserData :::: ${it.data}")
                        resultCallback.invoke(true)
                    }
                }
            }
        }
    }
}


@Composable
fun RestoreUserDataFromServer(
    email: String,
    wordViewModel: WordViewModel,
    resultCallback: (Boolean) -> Unit
) {
    val owner = LocalLifecycleOwner.current
    val ctx = LocalContext.current
    wordViewModel.restoreUserBackup(email).observe(owner) {
        when (it) {
            is NetworkResult.Error -> {
                timber("restoreUserBackup ERR ${it.message}")
                resultCallback.invoke(
                    false
                )
            }

            is NetworkResult.Loading -> {
                timber("restoreUserBackup Loading")
            }

            is NetworkResult.Success -> {
                timber("restoreUserBackup ${it.data}")

                if (it.data?.isSuccess.orFalse() && it.data?.data?.words != null) {
                    wordViewModel.clearWordsTable()
                    wordViewModel.clearTimesTable()
                    wordViewModel.clearVocabListsTable()
                    wordViewModel.addAllWords(it.data.data.words)
                    wordViewModel.addAllTimeSpent(it.data.data.totalTimeSpent ?: listOf())
                    wordViewModel.addAllVocabLists(it.data.data.vocabList ?: listOf())
                    resultCallback.invoke(
                        true
                    )

                } else {
                    ctx.toast(it.data?.message ?: ctx.getString(R.string.sth_wrong))
                    resultCallback.invoke(
                        false
                    )
                }

            }
        }
    }
}

@Composable
private fun ShareUserBackup(
    share: MutableState<Boolean> = remember { mutableStateOf(false) },
    wordViewModel: WordViewModel,
    resultCode: (Int?) -> Unit
) {
    timber("IS_SHARE_STATUS ${share.value}")
    val ctx = LocalContext.current
    val shareFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        resultCode.invoke(
            it.resultCode
        )
    }

    UserBackup(wordViewModel = wordViewModel) { data ->

        if (share.value) {
            val backupFile =
                File(ctx.getExternalFilesDir("")?.path.toString() + "/${Constant.Backup.BACKUP_ZIP_NAME}")
            val uri =
                FileProvider.getUriForFile(
                    ctx,
                    BuildConfig.APPLICATION_ID + ".provider",
                    backupFile
                )

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "application/zip" // MIME type for text files
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            resultCode.invoke(null)
            try {
                shareFileLauncher.launch(shareIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

}

@Composable
fun UserBackup(wordViewModel: WordViewModel, callBack: (BackupUserData) -> Unit) {
    val owner = LocalLifecycleOwner.current
    wordViewModel.getAllWords().observe(owner) { words ->
        wordViewModel.getAllValidTimeSpent().observe(owner) { times ->
            wordViewModel.getAllVocabList().observe(owner) { vocabList ->
                callBack.invoke(
                    BackupUserData(words = words, totalTimeSpent = times, vocabList = vocabList)
                )
            }
        }
    }
}


@Composable
fun ReadBackupFromStorage(launch: Boolean, isLaunched: () -> Unit = {}, data: (String?) -> Unit) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedUri ->
                val des =
                    File(context.getExternalFilesDir(null)?.absolutePath, "zipFile").absolutePath
                saveUriToFile(selectedUri, des, context)
                val json: String?
                val finalFile = File(context.getExternalFilesDir(""), "zipFile") // zip file
                unzip(finalFile.path, context.getExternalFilesDir("")?.path.orEmpty())

                val unZippedFile = File(context.getExternalFilesDir("backups"), "backup")
                unZippedFile.inputStream()
                json = try {
                    val inputStream: InputStream = unZippedFile.inputStream()
                    val size = inputStream.available()
                    val buffer = ByteArray(size)
                    inputStream.read(buffer)
                    inputStream.close()
                    String(buffer, charset("UTF-8"))
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                data.invoke(json)
            }
        }
    if (launch) {
        launcher.launch("application/zip")
        isLaunched.invoke()
    }
}

fun saveUriToFile(uri: Uri, destinationPath: String, context: Context) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(destinationPath)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    } catch (e: Exception) {
        // Handle exceptions accordingly
    }
}

@Throws(IOException::class)
fun copyFile(sourceFile: File, destFile: File) {
    val sourceChannel: FileChannel? = FileInputStream(sourceFile).channel
    val destChannel: FileChannel? = FileOutputStream(destFile).channel

    try {
        sourceChannel?.transferTo(0, sourceChannel.size(), destChannel)
    } finally {
        sourceChannel?.close()
        destChannel?.close()
    }
}

fun saveFileToCustomDirectory(sourceFile: File) {
    val subdirectoryName = BACKUP_FOLDER_NAME
    val newFileName = "${BACKUP_FILE_NAME_IN_DIRECTORY}${getCurrentDate()}.zip"
    val downloadDirectory =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    val customDirectory = File(downloadDirectory, subdirectoryName)

    // Create the custom directory if it doesn't exist
    if (!customDirectory.exists()) {
        customDirectory.mkdirs()
    }

    val destFile = File(customDirectory, newFileName)

    try {
        copyFile(sourceFile, destFile)
        // Optional: If you want to delete the original file after copying
        // sourceFile.delete()
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle the exception as needed
    }
}