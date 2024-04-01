package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.BackupOption
import com.ntg.vocabs.components.DividerLine
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.screens.setting.ReadBackupFromStorage
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.BackupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoBackupScreen(
    navController: NavController,
    backupViewModel: BackupViewModel
){

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.restore),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                navController = navController,
                backupViewModel
            )

        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    backupViewModel: BackupViewModel
){


    var importFromStorage by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        content = {

            item { 
                TypewriterText(
                    modifier = Modifier.padding(8.dp),
                    texts = listOf("Welcome back! Unfortunately, we couldn't find any backup for your email on our server."),
                    singleText = true)
            }

            item {
                BackupOption(
                    modifier = Modifier.padding(top = 24.dp),
                    title = stringResource(id = R.string.have_backup_drive),
                    subTitle = stringResource(
                        id = R.string.login_to_drive
                    )
                ) {
                    navController.navigate(Screens.AskBackupScreen.name)
                }

                BackupOption(
                    modifier = Modifier.padding(top = 8.dp),
                    title = stringResource(id = R.string.have_backup_phone),
                    subTitle = stringResource(
                        id = R.string.import_storage
                    )
                ) {
                    importFromStorage = true
                }
                
                DividerLine(
                    modifier = Modifier.padding(top = 24.dp),
                    title = stringResource(id = R.string.or))

                
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(id = R.string.no_backup_start_fresh),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                
                BackupOption(
                    modifier = Modifier.padding(top = 16.dp),
                    title = stringResource(id = R.string.select_backup_option)
                ) {
                    navController.navigate(Screens.SelectBackupOptionsScreen.name)
                }
            }
            
    })


    ReadBackupFromStorage(launch = importFromStorage, isLaunched = {importFromStorage = false}) {
        if (it.orEmpty().isNotEmpty()) {
            backupViewModel.importToDB(it!!) { isSucceed ->
                if (isSucceed) {
                    context.toast(R.string.backup_imported)
                    navController.navigate(Screens.VocabularyListScreen.name)
                } else {
                    context.toast(R.string.file_not_supported)
                }
            }
        }
    }

}