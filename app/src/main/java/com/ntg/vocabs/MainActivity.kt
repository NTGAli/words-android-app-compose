package com.ntg.vocabs

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.lifecycle.asLiveData
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
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
import com.ntg.vocabs.screens.setting.UserBackup
import com.ntg.vocabs.ui.theme.AppTheme
import com.ntg.vocabs.util.*
import com.ntg.vocabs.util.Constant.BACKUPS
import com.ntg.vocabs.util.backup.BackupWorker
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.CalendarViewModel
import com.ntg.vocabs.vm.DataViewModel
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.MessageBoxViewModel
import com.ntg.vocabs.vm.SignInViewModel
import com.ntg.vocabs.vm.WordViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.concurrent.TimeUnit


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
            AppTheme {

                val lists = wordViewModel.getAllVocabList().observeAsState()
                val startDes = remember {
                    mutableStateOf(Screens.SplashScreen.name)
                }
                val currentDes = remember {
                    mutableStateOf("")
                }

                listId = wordViewModel.currentList().observeAsState().value


                loginViewModel.getUserData().collectAsState(initial = null).let { userData ->

                    if (userData.value != null) {
                        if (userData.value?.email.orEmpty().isEmpty()) {
                            if (userData.value?.isSkipped.orFalse() && (lists.value?.size.orZero() != 0)) {

                                if (lists.value?.filter { it.isSelected }.orEmpty().isNotEmpty()) {
                                    startDes.value = Screens.HomeScreen.name
                                } else {
                                    startDes.value = Screens.VocabularyListScreen.name
                                }
                            } else {
                                startDes.value = Screens.InsertEmailScreen.name
                            }
                        } else if (lists.value?.size.orZero() == 0 || lists.value?.filter { it.isSelected }
                                .orEmpty().isEmpty()) {
                            startDes.value = Screens.VocabularyListScreen.name
                        } else {
                            startDes.value = Screens.HomeScreen.name
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
                        currentDes.value = navDestination.route.orEmpty()
                        currentScreen = navDestination.route.orEmpty()

                        when (navDestination.route) {
                            Screens.MessagesBoxScreen.name -> {
                                if (!checkInternet()) {
                                    navController.navigate(Screens.NoInternetConnection.name + "?screen=${navDestination.route}")
                                }
                            }

                        }

                        if (listId != null){
                            when(navDestination.route.orEmpty()){
                                Screens.LoginWithPasswordScreen.name,
                                Screens.InsertEmailScreen.name,
                                Screens.CodeScreen.name,
                                Screens.FinishScreen.name,
                                Screens.SplashScreen.name,
                                Screens.SplashScreen.name,
                                -> {
                                    timber("LoginPages")
                                }

                                Screens.RevisionScreen.name -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        calendarViewModel.insertSpendTime(SpendTimeType.Revision,listId!!.id)
                                    }
                                }

                                else ->{
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        calendarViewModel.insertSpendTime(SpendTimeType.Learning,listId!!.id)
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
                    wordViewModel.sizeGermanVerbs().observeAsState(initial = -1).value == 0
                    ){
                    val secondWorkerRequest = OneTimeWorkRequestBuilder<AutoInsertWorker>()
                        .build()
                    WorkManager.getInstance(this).enqueueUniqueWork(
                        "INSERTING_TO_DB",
                        ExistingWorkPolicy.KEEP,
                        secondWorkerRequest
                    )
                }
            }

            var backupUserData by remember {
                mutableStateOf<BackupUserData?>(null)
            }

            loginViewModel.getUserData().asLiveData().observeAsState().value.let { dataSettings ->
                if (dataSettings?.backupOption.orEmpty().isNotEmpty()) {

                    UserBackup(wordViewModel) {
                        if (it != backupUserData) {
                            backupUserData = it
                        }
                    }

                    if (dataSettings?.backupOption.orEmpty() != "Never" && dataSettings?.backupOption.orEmpty() != "Only when i tap ‘backup’") {
                        LaunchedEffect(key1 = Unit, block = {

                            val repeatTime = when (dataSettings?.backupOption) {
                                "Daily" -> 1L
                                "Weekly" -> 7L
                                "Monthly" -> 30L
                                else -> -1L
                            }

                            if (repeatTime != -1L) {
                                val constraints = Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()

                                val backupWorkRequest = PeriodicWorkRequestBuilder<BackupWorker>(
                                    repeatInterval = 16,
                                    repeatIntervalTimeUnit = TimeUnit.MINUTES
                                )
                                    .setConstraints(constraints)
                                    .build()

                                WorkManager.getInstance(this@MainActivity)
                                    .enqueueUniquePeriodicWork(
                                        "BackupOnDrive",
                                        ExistingPeriodicWorkPolicy.KEEP, backupWorkRequest
                                    )
                            }


                        })
                    }


                }
            }

            LaunchedEffect(key1 = backupUserData?.words, block = {
                timber("UserBackupUserBackupUserBackupUserBackup ::::")
                if (backupUserData?.words.orEmpty().isNotEmpty()){
                    saveBackupFile(backupUserData)
                }

            })
            var notificationStatusPermission by remember {
                mutableStateOf(false)
            }
            val notificationPermission =
                rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS, onPermissionResult = {
                    notificationStatusPermission = it
                })
            LaunchedEffect(key1 = Unit, block = {
                notificationPermission.launchPermissionRequest()
            })
        }



    }

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

}

