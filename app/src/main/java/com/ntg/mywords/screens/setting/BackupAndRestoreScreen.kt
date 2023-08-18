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
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.ItemOption
import com.ntg.mywords.util.timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupAndRestoreScreen(navController: NavController) {

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

            Content(paddingValues = innerPadding, navController)

        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController) {



    CheckWriteStoragePermission(
        onPermissionGranted = {
            // Permission granted, you can perform write operations here
                              timber("4444444444444444444444444444444444444444444444444444444444")
        },
        onPermissionDenied = {
            // Permission denied, show a message or take appropriate action
            timber("55555555555555555555555555555555555555555555555555555")

        }
    )





    val openDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    var filePath: Uri? = null

    LazyColumn(modifier = Modifier.padding(paddingValues)) {

        item {
            ItemOption(
                painter = painterResource(id = R.drawable.data_backup), text = stringResource(
                    id = R.string.backup_and_restore
                )
            ) {
                openDialog.value = true
            }
        }

    }


    // Request permission and save backup
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        } else {
            // Handle permission denied
            timber("DDDDDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEENNNNNNNNNNNNNNNNNNIIIIIIIIIIIIIIEEEEEEEEDDDDDDDDDD")
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//            intent.data = Uri.fromParts("package", context.packageName, null)
//            context.startActivity(intent)
        }
    }


    val pickPictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { imageUri ->
        if (imageUri != null) {
            // Update the state with the Uri
            timber("LOCATION_USERRRRRR :::: ${imageUri.path.orEmpty().toUri()}")

            filePath = imageUri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                timber("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL")

            }
//            AppDatabaseBackup.backupDatabase(context, filePath!!, activity = AppCompatActivity() )

//            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)


        }
    }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            icon = {},
            title = {
                Text(text = stringResource(id = R.string.backup))
            },
            text = {

                Column {
                    ItemOption(text = stringResource(id = R.string.backup_on_storage)) {
                        pickPictureLauncher.launch(null)
//                           AppDatabaseBackup.backupDatabase(context)
                    }

                    ItemOption(
                        text = stringResource(id = R.string.backup_on_server),
                        divider = false
                    ) {

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

//@Composable
//fun openFilePicker() {
//    val pickPictureLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.OpenDocumentTree()
//    ) { imageUri ->
//        if (imageUri != null) {
//            // Update the state with the Uri
//            timber("LOCATION_USERRRRRR :::: ${imageUri.path}")
//        }
//    }
//
//// In your button's click
//
//    Button(onClick = {
//        pickPictureLauncher.launch(null)
//
//    }){
//        Text(text = "CLICK")
//    }
//
//}




@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckWriteStoragePermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {
        Text("Camera permission Granted")
    } else {
        Column(Modifier.padding(top = 200.dp)) {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The camera is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Camera permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}

@Preview
@Composable
fun PreviewWriteStoragePermissionDemo() {
    timber("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLPP")
}