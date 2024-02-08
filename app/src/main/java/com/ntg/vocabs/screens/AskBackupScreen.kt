package com.ntg.vocabs.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.GoogleDriveSate
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.screens.setting.ReadBackupFromStorage
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontRegular12
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.util.unixTimeToReadable
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskBackupScreen(
    navController: NavController,
    backupViewModel: BackupViewModel,
    loginViewModel: LoginViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var option by remember {
        mutableStateOf("")
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Box(
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.padding(top = 32.dp, bottom = 24.dp),
                    painter = painterResource(id = R.drawable.google_drive),
                    contentDescription = null
                )
            }
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, backupViewModel, loginViewModel, navController)

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    paddingValues: PaddingValues,
    backupViewModel: BackupViewModel,
    loginViewModel: LoginViewModel,
    navController: NavController,
) {

    val context = LocalContext.current

    val backupOptions = listOf(
        "Daily",
        "Weekly",
        "Monthly",
        "Only when i tap ‘backup’",
        "Never"
    )


    var backupSelected by remember {
        mutableStateOf("")
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        BackupDialog(
            onDismissRequest = {
                showDialog = false
            },
            onConfirmation = {
                showDialog = false
                loginViewModel.setBackupOption("Never")
                navController.navigate(Screens.VocabularyListScreen.name)
            }
        )
    }

    var lastBackup by remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var openBottomSheet by remember {
        mutableStateOf(false)
    }

    var restoreOption by remember {
        mutableStateOf("")
    }

    backupViewModel.googleDriveState.asLiveData().observeAsState(initial = null).value.let {
        timber("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE $it")
        if (it == null) return@let
        if (backupSelected.isNotEmpty()){
            loginViewModel.setBackupOption(backupSelected)
        }
        else {
            loginViewModel.setBackupOption("Weekly")
        }
        if (it == GoogleDriveSate.FolderCreated) {
            loading = false
            navController.navigate(Screens.VocabularyListScreen.name)
        } else if (it == GoogleDriveSate.AlreadyExist) {
            backupViewModel.getFilesInFolder().observeAsState().value.let {
                if (it != null) {
                    if (it.isNotEmpty() && it.any { it.startsWith("VocabsBackup_") }) {
                        loading = false
                        lastBackup = getMaxBackup(it)
                    } else {
                        loading = false
                        navController.navigate(Screens.VocabularyListScreen.name)
                    }

                }
            }

        }
    }

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (result.data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)

                    task.isSuccessful
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                    backupViewModel.googleInstance(context)
                    backupViewModel.createFolder()

                } else {
                    Toast.makeText(context, "Google Login Error!", Toast.LENGTH_LONG).show()
                }
            } else {
                loading = false
            }
        }


    LaunchedEffect(key1 = restoreOption, block = {
        if (restoreOption != context.getString(R.string.str_import) && restoreOption.isNotEmpty()) {
            loading = true
            startForResult.launch(getGoogleSignInClient(context).signInIntent)
        }
    })


    ReadBackupFromStorage(launch = restoreOption == stringResource(id = R.string.str_import), isLaunched = {restoreOption = ""}) {
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


    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 32.dp), content = {

        item {
            TypewriterText(
                modifier = Modifier.padding(bottom = 8.dp),
                singleText = true,
                texts = if (lastBackup.isEmpty()) listOf(stringResource(id = R.string.back_up_drive_msg)) else listOf(
                    stringResource(id = R.string.alreadY_backup)
                ),
                speedType = 5
            )
        }

        if (lastBackup.isEmpty()) {
            items(backupOptions) {
                SampleItem(
                    title = it,
                    enableRadioButton = true,
                    radioSelect = mutableStateOf(backupSelected == it),
                    onClick = { text, _, isSelect ->
                        backupSelected = text
                    })

            }
        } else {
            item {
                Text(
                    text = getText(context, lastBackup),
                    style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        item {

            CustomButton(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                text = if (lastBackup.isEmpty()) {
                    if (backupSelected == "Never") stringResource(id = R.string.skip) else stringResource(
                        id = R.string.access
                    )
                } else stringResource(id = R.string.restore),
                type = ButtonType.Primary,
                size = ButtonSize.LG,
                loading = loading
            ) {
                if (lastBackup.isEmpty()) {
                    when (backupSelected) {

                        "" -> {
                            context.toast(R.string.select_backup)

                        }

                        "Never" -> {
                            showDialog = true

                        }

                        else -> {
                            loading = true
                            startForResult.launch(getGoogleSignInClient(context).signInIntent)
                        }

                    }
                } else {
                    loading = true
                    backupViewModel.restoreBackup(context, lastBackup) {
                        if (it != null) {
                            backupViewModel.importToDB(it) {
                                loading = false
                                ContextCompat.getMainExecutor(context).execute {
                                    navController.navigate(Screens.VocabularyListScreen.name)
                                }
                            }
                        } else {
                            loading = false
                            context.toast(R.string.sth_wrong)
                        }
                    }
                }
            }

            CustomButton(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = if (lastBackup.isEmpty()) stringResource(id = R.string.have_backup) else stringResource(
                    id = R.string.skip_backup
                ),
                type = ButtonType.Primary,
                style = ButtonStyle.TextOnly,
                size = ButtonSize.LG,
            ) {
                if (lastBackup.isEmpty()){
                    openBottomSheet = true
                }else{
                    navController.navigate(Screens.VocabularyListScreen.name)
                }
            }

        }

    })


    if (openBottomSheet && !loading) {

        val restoresOptions = listOf(
            stringResource(id = R.string.restore_from_drive),
            stringResource(id = R.string.str_import),
        )
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
                            text = stringResource(R.string.restore),
                            style = fontMedium14(
                                MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }

                    items(restoresOptions) {
                        SampleItem(
                            title = it,
                            onClick = { text, _, isSelect ->
                                restoreOption = text
                                openBottomSheet = false
                            })
                    }

                    item {
                        Spacer(modifier = Modifier.padding(24.dp))
                    }
                })

            }


        }
    }

}

@Composable
private fun BackupDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(painterResource(id = R.drawable.warning_error), contentDescription = null)
        },
        title = {
            Text(text = stringResource(id = R.string.are_sure))
        },
        text = {
            Text(text = stringResource(id = R.string.no_accsess_backup_msg))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(id = R.string.countinue_anyway))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.back))
            }
        }
    )
}

private fun getMaxBackup(backupList: List<String>): String {
    val validBackups = backupList.filter { isValidBackup(it) }
    return validBackups.maxByOrNull { extractNumber(it) } ?: "No valid backups found"
}

private fun isValidBackup(backup: String): Boolean {
    return backup.startsWith("VocabsBackup_") && backup.substringAfter("_").toIntOrNull() != null
}

private fun extractNumber(backup: String): Int {
    val numberPart = backup.substringAfter("_")
    return numberPart.toIntOrNull() ?: 0
}

private fun getText(context: Context, text: String): String {

    return try {
        context.getString(
            R.string.backup_details,
            text.split("_")[1].toLong().unixTimeToReadable()
        )
    } catch (e: Exception) {
        context.getString(
            R.string.want_to_restore
        )
    }
}