package com.ntg.vocabs

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.ntg.vocabs.model.SpendTimeType
import com.ntg.vocabs.model.db.TimeSpent
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
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDate
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


//                LaunchedEffect(userData) {
//                    delay(500)
//
//                }

                val navController = rememberNavController()

                Scaffold {
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

                        when (navDestination.route) {
                            Screens.MessagesBoxScreen.name -> {
                                if (!checkInternet()) {
//                                navController.popBackStack()
                                    navController.navigate(Screens.NoInternetConnection.name + "?screen=${navDestination.route}")
                                }
                            }

                        }

                    }
                }



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    HandleLifecycle(calendarViewModel, wordViewModel, currentDes.value)
                }


                if (intent.getStringExtra(Constant.ACTION).orEmpty().isNotEmpty()) {
                    startDes.value = Screens.MessagesBoxScreen.name
                    intent.action = ""
                    intent.putExtra(Constant.ACTION, "")
                }

                wordViewModel.getEnglishWordsSize().observeAsState(initial = -1).let {
                    timber("ENGLISH_WORD ::: ${it.value}")
                    if (it.value == 0) {
                        LaunchedEffect(key1 = it, block = {
                            wordViewModel.unzipRaw(resources.openRawResource(R.raw.en_words))
                        })
                    }
                }


                wordViewModel.getEnglishVerbsSize().observeAsState(initial = -1).let {
                    timber("ENGLISH_Verbs ::: ${it.value}")
                    if (it.value == 0) {
                        LaunchedEffect(key1 = it, block = {
                            wordViewModel.insertEnglishVerbsFromRes(resources.openRawResource(R.raw.word_forms))
                        })
                    }
                }

                wordViewModel.sizeGermanNoun().observeAsState(initial = -1).let {
                    timber("ENGLISH_Verbs ::: ${it.value}")
                    if (it.value == 0) {
                        LaunchedEffect(key1 = it, block = {
                            wordViewModel.unzipRaw(resources.openRawResource(R.raw.articles))
                        })
                    }
                }

                wordViewModel.sizeGermanVerbs().observeAsState(initial = -1).let {
                    timber("German_Verbs ::: ${it.value}")
                    if (it.value == 0) {
                        LaunchedEffect(key1 = it, block = {
                            wordViewModel.unzipRaw(resources.openRawResource(R.raw.combined_data))
                        })
                    }
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
                                val backupWorkRequest = PeriodicWorkRequestBuilder<BackupWorker>(
                                    repeatInterval = repeatTime,
                                    repeatIntervalTimeUnit = TimeUnit.MINUTES
                                ).build()

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
                saveBackupFile(backupUserData)

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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HandleLifecycle(
    calendarViewModel: CalendarViewModel, wordViewModel: WordViewModel, destination: String
) {

    val listId = wordViewModel.currentList().observeAsState().value
    val events = remember {
        mutableStateOf(Lifecycle.Event.ON_START)
    }

    if (listId?.id != null) {
        OnLifecycleEvent { owner, event ->
            events.value = event
        }
        when (events.value) {
            Lifecycle.Event.ON_START -> {
                calendarViewModel.removeNullTime()
            }

            Lifecycle.Event.ON_RESUME -> {
                LaunchedEffect(key1 = events) {
                    if (destination != Screens.RevisionScreen.name) {
                        delay(100)
                        calendarViewModel.insertSpendTime(
                            TimeSpent(
                                id = 0,
                                listId = listId.id,
                                date = LocalDate.now().toString(),
                                startUnix = System.currentTimeMillis(),
                                endUnix = null,
                                type = SpendTimeType.Learning.ordinal
                            )
                        )
                    }

                }
            }

            Lifecycle.Event.ON_STOP -> {
                calendarViewModel.stopLastTime()
            }

            Lifecycle.Event.ON_PAUSE -> {
                calendarViewModel.stopLastTime()
            }

            Lifecycle.Event.ON_DESTROY -> {
            }

            else -> {}
        }
    }


}
