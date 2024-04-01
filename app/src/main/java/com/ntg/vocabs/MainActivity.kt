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
import androidx.work.Data
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
import com.ntg.vocabs.util.backup.ServerBackupWorker
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

                var backupStatus by remember {
                    mutableStateOf<Boolean?>(null)
                }


                listId = wordViewModel.currentList().observeAsState().value

                val dataSettings = loginViewModel.getUserData().asLiveData().observeAsState().value
                (dataSettings?.backupOption.orEmpty() != "Never" && dataSettings?.backupOption.orEmpty()
                    .isNotEmpty()).let {
                    if (backupStatus == null) backupStatus = it
                }

                loginViewModel.getUserData().collectAsState(initial = null).let { userData ->
                    if (userData.value != null) {
                        val userDataValue = userData.value
                        if (userDataValue?.isIntroFinished.orTrue()) {

                            if (!userDataValue?.isSkipped.orFalse() && userDataValue?.email.orEmpty()
                                    .isEmpty()
                            ) {
                                startDes.value = Screens.InsertEmailScreen.name
                            }else if (userDataValue?.isSkipped.orFalse() && userDataValue?.backupOption.orEmpty().isEmpty()){
                                startDes.value = Screens.AskBackupScreen.name
                            }else if (lists.value?.filter { it.isSelected }.orEmpty()
                                    .isNotEmpty()
                            ) {
                                startDes.value = Screens.HomeScreen.name
                            } else if (userDataValue?.backupWay.orEmpty().isNotEmpty()){
                                startDes.value = Screens.VocabularyListScreen.name
                            }else if (!userDataValue?.isSubscriptionSkipped.orFalse()){
                                startDes.value = Screens.ExplainSubscriptionScreen.name
                            }else if (!backupStatus.orFalse()){
                                startDes.value = Screens.SelectBackupOptionsScreen.name
                            }else{
                                startDes.value = Screens.VocabularyListScreen.name
                            }

//                            if (userDataValue?.name.orEmpty().isEmpty()){
//                                startDes.value = Screens.NameScreen.name
//                            }else if (lists.value?.filter { it.isSelected }.orEmpty().isNotEmpty()){
//                                startDes.value = Screens.HomeScreen.name
//                            }else if (!userDataValue?.isSubscriptionSkipped.orFalse()){
//                                startDes.value = Screens.ExplainSubscriptionScreen.name
//                            }else if (!backupStatus.orFalse()){
//                                startDes.value = Screens.SelectBackupOptionsScreen.name
//                            }
//                            else{
//                                startDes.value = Screens.VocabularyListScreen.name
//                            }
                        } else {
                            startDes.value = Screens.IntroScreen.name
                        }

//                        if (userData.value?.email.orEmpty().isEmpty()) {
//                            if (userData.value?.isSkipped.orFalse() && (lists.value?.size.orZero() != 0)) {
//
//                                if (lists.value?.filter { it.isSelected }.orEmpty().isNotEmpty()) {
//                                    startDes.value = Screens.HomeScreen.name
//                                } else {
//                                    startDes.value = Screens.VocabularyListScreen.name
//                                }
//                            } else {
//                                startDes.value = Screens.InsertEmailScreen.name
//                            }
//                        } else if (lists.value?.size.orZero() == 0 || lists.value?.filter { it.isSelected }
//                                .orEmpty().isEmpty()) {
//                            startDes.value = Screens.VocabularyListScreen.name
//                        } else {
//                            startDes.value = Screens.HomeScreen.name
//                        }
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
                                Screens.InsertEmailScreen.name,
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

                    if (dataSettings?.backupOption.orEmpty() != "Never" && dataSettings?.backupOption.orEmpty() != "Only when i tap ‘backup’"
                        && dataSettings?.backupWay.orEmpty() != "no"
                        && dataSettings?.email.orEmpty().isNotEmpty()
                        && listId != null
                    ) {
                        LaunchedEffect(key1 = Unit, block = {
                            val constraints = Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                            if (dataSettings?.backupWay == "drive") {
                                val repeatTime = when (dataSettings?.backupOption) {
                                    "Daily" -> 1L
                                    "Weekly" -> 7L
                                    "Monthly" -> 30L
                                    else -> -1L
                                }

                                if (repeatTime != -1L) {
                                    val backupWorkRequest =
                                        PeriodicWorkRequestBuilder<BackupWorker>(
                                            repeatInterval = repeatTime,
                                            repeatIntervalTimeUnit = TimeUnit.DAYS
                                        )
                                            .setConstraints(constraints)
                                            .build()

                                    WorkManager.getInstance(this@MainActivity)
                                        .enqueueUniquePeriodicWork(
                                            "BackupOnDrive",
                                            ExistingPeriodicWorkPolicy.KEEP, backupWorkRequest
                                        )
                                }
                            } else {
                                val data = Data.Builder()
                                data.putString("email", dataSettings?.email)

                                val backupWorkRequest =
                                    PeriodicWorkRequestBuilder<ServerBackupWorker>(
                                        repeatInterval = 7,
                                        repeatIntervalTimeUnit = TimeUnit.DAYS
                                    ).setInputData(data.build())

                                        .setConstraints(constraints)
                                        .build()




                                WorkManager.getInstance(this@MainActivity)
                                    .enqueueUniquePeriodicWork(
                                        "BackupOnServer",
                                        ExistingPeriodicWorkPolicy.KEEP, backupWorkRequest
                                    )
                            }

                        })
                    }


                }
            }

            LaunchedEffect(key1 = backupUserData?.words, block = {
                timber("UserBackupUserBackupUserBackupUserBackup ::::")
                if (backupUserData?.words.orEmpty().isNotEmpty()) {
                    saveBackupFile(backupUserData)
                }

            })
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

    override fun onResume() {
        super.onResume()
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

    override fun onPause() {
        super.onPause()
        if (listId != null) {
            calendarViewModel.stopLastTime()
        }

    }

}

