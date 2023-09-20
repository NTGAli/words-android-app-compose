package com.ntg.mywords

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.ntg.mywords.di.SettingsSerializer
import com.ntg.mywords.model.SpendTimeType
import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.nav.AppNavHost
import com.ntg.mywords.ui.theme.AppTheme
import com.ntg.mywords.util.*
import com.ntg.mywords.util.Constant.DATA_STORE_FILE_NAME
import com.ntg.mywords.vm.CalendarViewModel
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.SignInViewModel
import com.ntg.mywords.vm.WordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val wordViewModel: WordViewModel by viewModels()
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                AppNavHost(
                    wordViewModel = wordViewModel,
                    calendarViewModel = calendarViewModel,
                    loginViewModel = loginViewModel,
                    signInViewModel = signInViewModel
                ) { _, navDestination, _ ->
                    timber("onDestinationChangeListener ${navDestination.route}")
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                HandleLifecycle(calendarViewModel, wordViewModel)
            }


        }



    }

//    fun setTheme() {
//        val dataStore = UserStore(this)
//        dataStore.getAccessToken.asLiveData().observeForever {
//            when (it) {
//                getString(R.string.light_mode) -> {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                }
//                getString(R.string.dark_mode) -> {
//                    timber("wajhdlawfjhalwfjkhwkjahfsssssssssssssskwajhf $it")
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                }
//                else -> {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//                }
//            }
//        }
//    }



//    override fun setTheme(resid: Int) {
//        try {
//            when (loginViewModel.getTheme().value) {
//                getString(R.string.light_mode) -> {
//                    super.setTheme(R.style.Theme_MyWordsLight)
//                }
//                getString(R.string.dark_mode) -> {
//                    super.setTheme(R.style.Theme_MyWordsDark)
//                }
//                else -> {
//                    super.setTheme(R.style.Theme_MyWords)
//                }
//            }
//        }catch (e: Exception){
//
//        }
//
//    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HandleLifecycle(calendarViewModel: CalendarViewModel, wordViewModel: WordViewModel) {
    val listId = wordViewModel.getIdOfListSelected().observeAsState().value
    val events = remember {
        mutableStateOf(Lifecycle.Event.ON_START)
    }

    OnLifecycleEvent { owner, event ->
        events.value = event
    }

    timber("awawldjlkawjdlkjwald ${events.value.name}")

    when (events.value) {
        Lifecycle.Event.ON_START -> {
            calendarViewModel.removeNullTime()
        }

        Lifecycle.Event.ON_RESUME -> {
            LaunchedEffect(key1 = events) {
//                calendarViewModel.stopLastTime()
                delay(100)
                calendarViewModel.insertSpendTime(
                    TimeSpent(
                        id = 0,
                        listId = listId?.id.orZero(),
                        date = LocalDate.now().toString(),
                        startUnix = System.currentTimeMillis(),
                        endUnix = null,
                        type = SpendTimeType.Learning.ordinal
                    )
                )
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


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun DefaultPreview() {
    AppTheme {
        Greeting("Android")
    }
}

