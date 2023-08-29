package com.ntg.mywords.screens.setting

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

import com.ntg.mywords.R
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.ItemOption
import com.ntg.mywords.model.req.BackupUserData
import com.ntg.mywords.util.timber
import com.ntg.mywords.util.toast
import com.ntg.mywords.vm.WordViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupAndRestoreScreen(navController: NavController, wordViewModel: WordViewModel) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.setting),
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, navController, wordViewModel)

        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController, wordViewModel: WordViewModel) {

    val ctx = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    val setBackupOnServer = remember {
        mutableStateOf(false)
    }




    if (setBackupOnServer.value){
        BackupOnServer(wordViewModel){
            setBackupOnServer.value = false
            if (it){
                ctx.toast(ctx.getString(R.string.backup_done))
            }else{
                ctx.toast(ctx.getString(R.string.backup_failed))
            }
        }
    }







    LazyColumn(modifier = Modifier.padding(paddingValues)) {

        item {
            ItemOption(modifier = Modifier.padding(horizontal = 32.dp), text = stringResource(
                    id = R.string.backup
                )
            ) {
                openDialog.value = true
            }
        }

    }





    if (openDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 16.dp),
            onDismissRequest = {
                openDialog.value = false
            },
            icon = {},
            title = {
                Text(text = stringResource(id = R.string.backup))
            },
            text = {

                Column {


                    ItemOption(
                        text = stringResource(id = R.string.backup_on_server),
                        loading = setBackupOnServer
                    ) {
                        setBackupOnServer.value = true
                    }

                    ItemOption(text = stringResource(id = R.string.share), divider = false) {

                    }

                }

            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
private fun BackupOnServer(wordViewModel: WordViewModel, resultCallback:(Boolean) -> Unit){
    val owner = LocalLifecycleOwner.current
    wordViewModel.getMyWords().observe(owner) { words ->
        wordViewModel.getAllValidTimeSpent().observe(owner) { times ->
            val wordData = BackupUserData(words = words, totalTimeSpent = times)
            wordViewModel.upload(wordData, "alintg14@gmail.com").observe(owner) {

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