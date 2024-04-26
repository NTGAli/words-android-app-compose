package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.LoginViewModel

@Composable
fun RestoringBackupOnServerScreen(
    navController: NavController,
    backupViewModel: BackupViewModel,
    loginViewModel: LoginViewModel,
    email: String,
) {

    if (email.isEmpty()) return

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit, block = {

        backupViewModel.restoreBackupFromServer(context, email){
            context.toast(R.string.sth_wrong)
        }

        backupViewModel.restoreVocabularies(email){
            loginViewModel.checkBackup(it)
        }
    })

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            CircularProgressIndicator(
                modifier = Modifier
                    .progressSemantics()
                    .size(24.dp), color = MaterialTheme.colorScheme.onBackground, strokeWidth = 3.dp
            )

            Text(text = stringResource(id = R.string.restoring_backup))
        }

    }

}