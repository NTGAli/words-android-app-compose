package com.ntg.vocabs

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.asLiveData
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.db.AutoInsertWorker
import com.ntg.vocabs.model.SpendTimeType
import com.ntg.vocabs.model.db.VocabItemList
import com.ntg.vocabs.model.req.BackupUserData
import com.ntg.vocabs.nav.AppNavHost
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.services.AlarmReceiver
import com.ntg.vocabs.services.ReminderService
import com.ntg.vocabs.ui.theme.AppTheme
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.Constant.BACKUPS
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_LISTS
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_TIMES
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_WORDS
import com.ntg.vocabs.util.checkInternet
import com.ntg.vocabs.util.getFormattedDateInString
import com.ntg.vocabs.util.nextTenMinutes
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orTrue
import com.ntg.vocabs.util.showToastMessage
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.worker.FirebaseBackupWorker
import com.ntg.vocabs.util.worker.MediaBackupWorker
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
import java.util.Calendar


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
    private var revisionState = false
    private var isChecked = false
    private lateinit var alarmManager: AlarmManager
    private val myCalendar = Calendar.getInstance()


    @OptIn(ExperimentalPermissionsApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val dataSettings = loginViewModel.getUserData().collectAsState(initial = null)
            listId = wordViewModel.currentList().observeAsState().value


            AppTheme {

                val lists = wordViewModel.getAllVocabList().observeAsState()
                val startDes = remember {
                    mutableStateOf(Screens.SplashScreen.name)
                }
                val currentDes = remember {
                    mutableStateOf("")
                }
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
                    Column {
//                        CustomButton(text = "test"){
//                            setRemainderAlarm("sample23", "test23", 1214,this@MainActivity)
//                        }
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

                                with(navDestination.route.orEmpty()) {

                                    timber("CCCCCCCCCCCCCCCC ::::: $this ------ ${this.contains(Screens.RevisionScreen.name)}")
                                    when {
                                        contains(Screens.LoginWithPasswordScreen.name) ||
                                                contains(Screens.GoogleLoginScreen.name) ||
                                                contains(Screens.CodeScreen.name) ||
                                                contains(Screens.FinishScreen.name) ||
                                                contains(Screens.SplashScreen.name) ||
                                                contains(Screens.SplashScreen.name)
                                        -> {
                                            timber("LoginPages")
                                        }

                                        contains(Screens.RevisionScreen.name) ||
                                                contains(Screens.WritingScreen.name)
                                        -> {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && startTime != 0L) {
                                                calendarViewModel.insertSpendTime(
                                                    SpendTimeType.Learning,
                                                    listId!!.id,
                                                    startTime
                                                )
                                            }
                                            startTime = System.currentTimeMillis()
                                            revisionState = true
                                        }

                                        else -> {

                                            if (startTime != 0L){
                                                if (revisionState){
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        calendarViewModel.insertSpendTime(
                                                            SpendTimeType.Revision,
                                                            listId!!.id,
                                                            startTime
                                                        )
                                                    }
                                                    revisionState = false
                                                }else{
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        calendarViewModel.insertSpendTime(
                                                            SpendTimeType.Learning,
                                                            listId!!.id,
                                                            startTime
                                                        )
                                                    }
                                                }
                                            }
                                            startTime = System.currentTimeMillis()
                                        }

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

            dataSettings.value.let { dataSettings ->
                timber("getUnSyncedWords ----> ${dataSettings?.email}")
                if (dataSettings?.email != null) {

                    LaunchedEffect(key1 = Unit, block = {
                        syncData(dataSettings.email, BACKUP_WORDS, this@MainActivity)
                        syncData(dataSettings.email, BACKUP_LISTS, this@MainActivity)
                        syncMedia(dataSettings.email, this@MainActivity)
                        if (dataSettings.isPurchased) {
                            syncData(dataSettings.email, BACKUP_TIMES, this@MainActivity)
                        }
                    })

                }
            }
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

        loginViewModel.getUserData().asLiveData().observe(this) {
            if (it == null && isChecked) return@observe
            loginViewModel.checkIsVipUsers(it.email)
            isChecked = true
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

    private var startTime = 0L

    override fun onResume() {
        super.onResume()
        if (calendarViewModel.currentScreen != null && listId != null) {
            startTime = System.currentTimeMillis()
        }
    }

    override fun onPause() {
        super.onPause()
        if (calendarViewModel.currentScreen != null && listId != null && startTime != 0L && startTime < System.currentTimeMillis()) {
            when (calendarViewModel.currentScreen) {
                Screens.RevisionScreen.name,
                Screens.WritingScreen.name,
                -> {
                    startTime = System.currentTimeMillis()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        calendarViewModel.insertSpendTime(
                            SpendTimeType.Revision,
                            listId!!.id,
                            startTime
                        )
                    }
                }

                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        calendarViewModel.insertSpendTime(
                            SpendTimeType.Learning,
                            listId!!.id,
                            startTime
                        )
                    }
                }
            }
        }
    }


    private fun setRemainderAlarm(savedReminderId: Long) {
        myCalendar.add(Calendar.MINUTE, 3)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderService = ReminderService()
        val reminderReceiverIntent = Intent(this, AlarmReceiver::class.java)

        reminderReceiverIntent.putExtra("reminderId", savedReminderId)
        reminderReceiverIntent.putExtra("isServiceRunning", isServiceRunning(reminderService))
        val pendingIntent =
            PendingIntent.getBroadcast(this, savedReminderId.toInt(), reminderReceiverIntent,
                PendingIntent.FLAG_IMMUTABLE)
        val formattedDate = getFormattedDateInString(myCalendar.timeInMillis, "dd/MM/YYYY HH:mm")
        Log.d("TimeSetInMillis:", formattedDate)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, myCalendar.timeInMillis, pendingIntent
            )
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, myCalendar.timeInMillis, pendingIntent)
        }

        showToastMessage(this, "Alarm is set at : $formattedDate")
        finish()
    }


    fun setRemainderAlarm(word: String, type: String,id: Int, context: Context) {
        myCalendar.add(Calendar.MINUTE, 3)
//    myCalendar.set(Calendar.HOUR_OF_DAY, 10)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderService = ReminderService()
        val reminderReceiverIntent = Intent(this, AlarmReceiver::class.java)

        reminderReceiverIntent.putExtra("word", word)
        reminderReceiverIntent.putExtra("type", type)
        reminderReceiverIntent.putExtra("id", id)
        reminderReceiverIntent.putExtra("isServiceRunning",
            com.ntg.vocabs.util.isServiceRunning(reminderService, this)
        )
        val pendingIntent =
            PendingIntent.getBroadcast(this, id, reminderReceiverIntent,
                PendingIntent.FLAG_IMMUTABLE)
        val formattedDate = getFormattedDateInString(myCalendar.timeInMillis, "dd/MM/YYYY HH:mm")
        timber("TimeSetInMillis:", "$formattedDate --- $id")

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, myCalendar.timeInMillis, pendingIntent
        )
    }

    @Suppress("DEPRECATION")
    private fun isServiceRunning(reminderService: ReminderService): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (reminderService.javaClass.name == service.service.className) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.i("isMyServiceRunning?", false.toString() + "")
        return false
    }

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
            "backupWork_$type",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
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


