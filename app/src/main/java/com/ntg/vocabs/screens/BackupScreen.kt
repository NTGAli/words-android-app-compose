package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.ntg.vocabs.components.BackupReportItem
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.ui.theme.fontRegular12
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.isInternetAvailable
import com.ntg.vocabs.util.orTrue
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.MessageBoxViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    navController: NavController,
    backupViewModel: BackupViewModel
) {
    var loading by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.google_drive_backup),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, backupViewModel)
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                Divider(
                    Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                CustomButton(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.backup_now),
                    size = ButtonSize.XL,
                    loading = loading
                ) {
                    try {
                        if (isInternetAvailable(context)) {
                            backupViewModel.googleInstance(context)
                            loading = true
                            val dataFolder = File(context.getExternalFilesDir(""), "backups")
                            val file = File(dataFolder, Constant.BACKUPS)
                            backupViewModel.backupDB(file) {
                                loading = false
                            }
                        }
                        else{
                            context.toast(R.string.no_internet)
                        }
                    } catch (e: Exception) {
                        loading = false
                    }
                }

            }
        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    backupViewModel: BackupViewModel
) {

    val backups = backupViewModel.getAllBackups().observeAsState(initial = null).value

    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 24.dp), content = {

        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                text = stringResource(id = R.string.download_backup_drive_msg),
                style = fontRegular12(
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        items(backups.orEmpty()) {
            BackupReportItem(
                modifier = Modifier.padding(bottom = 8.dp),
                id = it.id,
                subTitle = it.time,
                tertiaryText = it.description,
                isSuccess = it.isSuccess
            )
        }

        item {
            Spacer(modifier = Modifier.padding(32.dp))
        }

    })

}
