package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.work.WorkManager
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.BackupOption
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBackupOptionsScreen(
    navController: NavController,
    backupViewModel: BackupViewModel,
    loginViewModel: LoginViewModel
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.backup_options),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, navController, backupViewModel, loginViewModel)

        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    backupViewModel: BackupViewModel,
    loginViewModel: LoginViewModel
) {

    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 32.dp),
        content = {

            item {


                TypewriterText(
                    modifier = Modifier.padding(top = 16.dp),
                    texts = listOf("Select a backup option"), singleText = true)

                BackupOption(
                    modifier = Modifier.padding(top = 32.dp),
                    title = stringResource(id = R.string.backup_on_server),
                    subTitle = stringResource(
                        id = R.string.login_to_app
                    )
                ) {
                    loginViewModel.setBackupWay("server")
                    WorkManager.getInstance(context).cancelAllWorkByTag("BackupOnDrive")
                    navController.navigate(Screens.VocabularyListScreen.name)
                }

                BackupOption(
                    modifier = Modifier.padding(top = 8.dp),
                    title = stringResource(id = R.string.on_google_drive),
                    subTitle = stringResource(
                        id = R.string.login_to_drive
                    )
                ) {
                    loginViewModel.setBackupWay("drive")
                    navController.navigate(Screens.AskBackupScreen.name)
                }

                BackupOption(
                    modifier = Modifier.padding(top = 8.dp),
                    title = stringResource(id = R.string.manual_backup),
                    subTitle = stringResource(
                        id = R.string.manual_desc
                    )
                ) {
                    loginViewModel.setBackupWay("no")
                    navController.navigate(Screens.VocabularyListScreen.name)
                }

            }

        })

}