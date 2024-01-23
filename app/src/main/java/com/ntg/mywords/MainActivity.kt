package com.ntg.mywords

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
import com.ntg.mywords.model.SpendTimeType
import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.nav.AppNavHost
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.playback.AndroidAudioPlayer
import com.ntg.mywords.record.AndroidAudioRecorder
import com.ntg.mywords.record.AudioRecorder
import com.ntg.mywords.ui.theme.AppTheme
import com.ntg.mywords.util.*
import com.ntg.mywords.vm.CalendarViewModel
import com.ntg.mywords.vm.DataViewModel
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.MessageBoxViewModel
import com.ntg.mywords.vm.SignInViewModel
import com.ntg.mywords.vm.WordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.io.File
import java.time.LocalDate


@AndroidEntryPoint
class MainActivity : ComponentActivity(),AudioRecorder {

    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private var audioFile: File? = null

    private val wordViewModel: WordViewModel by viewModels()
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val signInViewModel: SignInViewModel by viewModels()
    private val messageBoxViewModel: MessageBoxViewModel by viewModels()
    private val dataViewModel: DataViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val userData = loginViewModel.getUserData().asLiveData().observeAsState()
                val lists = wordViewModel.getAllVocabList().observeAsState()
                val startDes = remember {
                    mutableStateOf(Screens.SplashScreen.name)
                }
                val currentDes = remember {
                    mutableStateOf("")
                }

                timber("VOCAB_LISTS ::::::: $lists")
                timber("USER_EMAIL :::::::: ${userData.value?.email}")
                timber("USER_NAME :::::::: ${userData.value?.name}")

                LaunchedEffect(userData) {
                    delay(500)
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
                        startDestination = startDes.value
                    ) { _, navDestination, _ ->


                        if (navDestination.route.orEmpty() == currentDes.value) return@AppNavHost

                        timber("onDestinationChangeListener ${navDestination.route}")
                        currentDes.value = navDestination.route.orEmpty()

                        when (navDestination.route) {
                            Screens.MessagesBoxScreen.name -> {
                                if (!checkInternet()){
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

            }
        }
    }

    override fun start(outputFile: File) {
        timber("waljdjkawhdjkhawjkdhkawjhdkjwahd SSS")
    }

    override fun stop() {
        timber("waljdjkawhdjkhawjkdhkawjhdkjwahd PPPP")
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
