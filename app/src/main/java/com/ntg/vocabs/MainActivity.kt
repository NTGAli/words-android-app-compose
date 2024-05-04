package com.ntg.vocabs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import com.ntg.vocabs.db.AutoInsertWorker
import com.ntg.vocabs.model.SpendTimeType
import com.ntg.vocabs.model.db.VocabItemList
import com.ntg.vocabs.model.req.BackupUserData
import com.ntg.vocabs.nav.AppNavHost
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.AppTheme
import com.ntg.vocabs.util.*
import com.ntg.vocabs.util.Constant.BACKUPS
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_LISTS
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_MEDIA
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_WORDS
import com.ntg.vocabs.util.backup.BackupWorker
import com.ntg.vocabs.util.backup.FirebaseBackupWorker
import com.ntg.vocabs.util.backup.MediaBackupWorker
import com.ntg.vocabs.util.backup.StopSpendTimeWorker
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.CalendarViewModel
import com.ntg.vocabs.vm.DataViewModel
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.MessageBoxViewModel
import com.ntg.vocabs.vm.SignInViewModel
import com.ntg.vocabs.vm.WordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val wordViewModel: WordViewModel by viewModels()
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val signInViewModel: SignInViewModel by viewModels()
    private val messageBoxViewModel: MessageBoxViewModel by viewModels()
    private val dataViewModel: DataViewModel by viewModels()
    private val backupViewModel: BackupViewModel by viewModels()

    private var currentScreen = Screens.HomeScreen.name
    var listId: VocabItemList? = null


    @OptIn(ExperimentalPermissionsApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val dataSettings = loginViewModel.getUserData().collectAsState(initial = null)

            AppTheme {

                val lists = wordViewModel.getAllVocabList().observeAsState()
                val startDes = remember {
                    mutableStateOf(Screens.SplashScreen.name)
                }
                val currentDes = remember {
                    mutableStateOf("")
                }
                listId = wordViewModel.currentList().observeAsState().value
                dataSettings.let { userData ->
                    if (userData.value != null) {
                        val userDataValue = userData.value
                        if (userDataValue?.isIntroFinished.orTrue()) {

                            if (!userDataValue?.isSkipped.orFalse() && userDataValue?.email.orEmpty()
                                    .isEmpty()
                            ) {
                                startDes.value = Screens.GoogleLoginScreen.name
                            } else if (!userData.value?.isSubscriptionSkipped.orTrue()) {
                                startDes.value = Screens.ExplainSubscriptionScreen.name
                            } else if (lists.value?.filter { it.isSelected }.orEmpty()
                                    .isEmpty()
                            ) {
                                startDes.value = Screens.VocabularyListScreen.name
                            } else {
                                startDes.value = Screens.HomeScreen.name
                            }
                        } else {
                            startDes.value = Screens.IntroScreen.name
                        }
                    }

                    timber("VOCAB_LISTS ::::::: $lists")
                    timber("USER_EMAIL :::::::: ${userData.value?.email}")
                    timber("USER_NAME :::::::: ${userData.value?.name}")

                }
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    AppNavHost(
                        wordViewModel = wordViewModel,
                        navController = navController,
                        calendarViewModel = calendarViewModel,
                        loginViewModel = loginViewModel,
                        signInViewModel = signInViewModel,
                        messageBoxViewModel = messageBoxViewModel,
                        dataViewModel = dataViewModel,
                        backupViewModel = backupViewModel,
                        startDestination = startDes.value
                    ) { _, navDestination, _ ->


                        if (navDestination.route.orEmpty() == currentDes.value) return@AppNavHost

                        timber("onDestinationChangeListener ${navDestination.route}")
                        calendarViewModel.currentScreen = navDestination.route
                        currentDes.value = navDestination.route.orEmpty()
                        currentScreen = navDestination.route.orEmpty()

                        when (navDestination.route) {
                            Screens.MessagesBoxScreen.name -> {
                                if (!checkInternet()) {
                                    navController.navigate(Screens.NoInternetConnection.name + "?screen=${navDestination.route}")
                                }
                            }

                        }

                        if (listId != null) {
                            when (navDestination.route.orEmpty()) {
                                Screens.LoginWithPasswordScreen.name,
                                Screens.GoogleLoginScreen.name,
                                Screens.CodeScreen.name,
                                Screens.FinishScreen.name,
                                Screens.SplashScreen.name,
                                Screens.SplashScreen.name,
                                -> {
                                    timber("LoginPages")
                                }

                                Screens.RevisionScreen.name,
                                Screens.WritingScreen.name,
                                -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        calendarViewModel.insertSpendTime(
                                            SpendTimeType.Revision,
                                            listId!!.id
                                        )
                                    }
                                }

                                else -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        calendarViewModel.insertSpendTime(
                                            SpendTimeType.Learning,
                                            listId!!.id
                                        )
                                    }
                                }

                            }
                        }

                    }
                }

                if (intent.getStringExtra(Constant.ACTION).orEmpty().isNotEmpty()) {
                    startDes.value = Screens.MessagesBoxScreen.name
                    intent.action = ""
                    intent.putExtra(Constant.ACTION, "")
                }


                if (wordViewModel.getEnglishWordsSize().observeAsState(initial = -1).value == 0 ||
                    wordViewModel.getEnglishVerbsSize().observeAsState(initial = -1).value == 0 ||
                    wordViewModel.sizeGermanNoun().observeAsState(initial = -1).value == 0 ||
                    wordViewModel.sizeGermanVerbs().observeAsState(initial = -1).value == 0 ||
                    wordViewModel.sizeSounds().observeAsState(initial = -1).value == 0
                ) {
                    val secondWorkerRequest = OneTimeWorkRequestBuilder<AutoInsertWorker>()
                        .build()
                    WorkManager.getInstance(this).enqueueUniqueWork(
                        "INSERTING_TO_DB",
                        ExistingWorkPolicy.KEEP,
                        secondWorkerRequest
                    )
                }
            }

//            var backupUserData by remember {
//                mutableStateOf<BackupUserData?>(null)
//            }

            dataSettings.value.let { dataSettings ->
                timber("getUnSyncedWords ----> ${dataSettings?.email}")
                if (dataSettings?.email != null) {

                    LaunchedEffect(key1 = Unit, block = {
                        syncData(dataSettings.email, BACKUP_WORDS, this@MainActivity)
                        syncData(dataSettings.email, BACKUP_LISTS, this@MainActivity)
                        syncMedia(dataSettings.email, this@MainActivity)
                    })

                }
//
//                    UserBackup(wordViewModel) {
//                        if (it != backupUserData) {
//                            backupUserData = it
//                        }
//                    }
//
//                    if (dataSettings?.backupOption.orEmpty() != "Never" && dataSettings?.backupOption.orEmpty() != "Only when i tap ‘backup’"
//                        && dataSettings?.backupWay.orEmpty() != "no"
//                        && dataSettings?.email.orEmpty().isNotEmpty()
//                        && listId != null
//                    ) {
//                        LaunchedEffect(key1 = Unit, block = {
//                            val constraints = Constraints.Builder()
//                                .setRequiredNetworkType(NetworkType.CONNECTED)
//                                .build()
//                            if (dataSettings?.backupWay == "drive") {
//                                val repeatTime = when (dataSettings?.backupOption) {
//                                    "Daily" -> 1L
//                                    "Weekly" -> 7L
//                                    "Monthly" -> 30L
//                                    else -> -1L
//                                }
//
//                                if (repeatTime != -1L) {
//                                    val backupWorkRequest =
//                                        PeriodicWorkRequestBuilder<BackupWorker>(
//                                            repeatInterval = repeatTime,
//                                            repeatIntervalTimeUnit = TimeUnit.DAYS
//                                        )
//                                            .setConstraints(constraints)
//                                            .build()
//
//                                    WorkManager.getInstance(this@MainActivity)
//                                        .enqueueUniquePeriodicWork(
//                                            "BackupOnDrive",
//                                            ExistingPeriodicWorkPolicy.KEEP, backupWorkRequest
//                                        )
//                                }
//                            } else {
//                                val data = Data.Builder()
//                                data.putString("email", dataSettings?.email)
//
//                                val backupWorkRequest =
//                                    PeriodicWorkRequestBuilder<ServerBackupWorker>(
//                                        repeatInterval = 7,
//                                        repeatIntervalTimeUnit = TimeUnit.DAYS
//                                    ).setInputData(data.build())
//
//                                        .setConstraints(constraints)
//                                        .build()
//
//
//
//
//                                WorkManager.getInstance(this@MainActivity)
//                                    .enqueueUniquePeriodicWork(
//                                        "BackupOnServer",
//                                        ExistingPeriodicWorkPolicy.KEEP, backupWorkRequest
//                                    )
//                            }
//
//                        })
//                    }
//
//
//                }
            }

//            LaunchedEffect(key1 = backupUserData?.words, block = {
//                timber("UserBackupUserBackupUserBackupUserBackup ::::")
//                if (backupUserData?.words.orEmpty().isNotEmpty()) {
//                    saveBackupFile(backupUserData)
//                }
//
//            })
            var notificationStatusPermission by remember {
                mutableStateOf(false)
            }
            val notificationPermission =
                rememberPermissionState(
                    Manifest.permission.POST_NOTIFICATIONS,
                    onPermissionResult = {
                        notificationStatusPermission = it
                    })
            LaunchedEffect(key1 = Unit, block = {
                notificationPermission.launchPermissionRequest()
            })

            OnLifecycleEvent{owner, event ->
                if (event == Lifecycle.Event.ON_PAUSE){
                    if (listId != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            calendarViewModel.stopLastTime()
                        }
                    }
                }
            }
        }


    }


    /*
    save data in database in file as text
    */
    private fun saveBackupFile(backupUserData: BackupUserData?) {

        if (backupUserData == null) return


        val json = Gson().toJson(backupUserData)
        val dataFolder = File(getExternalFilesDir(""), "backups")

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
        val file = File(dataFolder, BACKUPS)
        try {
            val fileWriter = FileWriter(file)
            fileWriter.write(json)
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        timber("calendarViewModellll :::: res")
        if (calendarViewModel.currentScreen != null && listId != null) {
            when (calendarViewModel.currentScreen) {
                Screens.RevisionScreen.name,
                Screens.WritingScreen.name,
                -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        calendarViewModel.insertSpendTime(SpendTimeType.Revision, listId!!.id)
                    }
                }

                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        calendarViewModel.insertSpendTime(SpendTimeType.Learning, listId!!.id)
                    }
                }
            }
        }
    }

//    override fun onPause() {
//        super.onPause()
//        if (listId != null) {
//            CoroutineScope(Dispatchers.IO).launch {
//                calendarViewModel.stopLastTime()
//            }
//        }
//    }

}

fun syncData(email: String, type: String, context: Context) {
    timber("getUnSyncedWords :::: sync")

    val data = Data.Builder()
    data.putString("email", email)
    data.putString("type", type)

    val backupWorkRequest = OneTimeWorkRequestBuilder<FirebaseBackupWorker>()
        .setInputData(data.build())
        .setConstraints(Constraints.Builder().build()) // Optionally, set constraints
        .build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            "backupWork_$type", // Unique name for this work
            ExistingWorkPolicy.APPEND_OR_REPLACE, // Append or replace existing work with new one
            backupWorkRequest
        )
}

fun syncMedia(email: String, context: Context) {
    timber("getUnSyncedWords :::: sync")

    val data = Data.Builder()
    data.putString("email", email)

    val backupWorkRequest = OneTimeWorkRequestBuilder<MediaBackupWorker>()
        .setInputData(data.build())
        .setConstraints(Constraints.Builder().build())
        .build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            "mediaBackupWorker",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            backupWorkRequest
        )
}


