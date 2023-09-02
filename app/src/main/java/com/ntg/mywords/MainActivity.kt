package com.ntg.mywords

import android.content.Context
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import com.ntg.mywords.di.SettingsSerializer
import com.ntg.mywords.model.SpendTimeType
import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.nav.AppNavHost
import com.ntg.mywords.ui.theme.AppTheme
import com.ntg.mywords.util.Constant
import com.ntg.mywords.util.Constant.DATA_STORE_FILE_NAME
import com.ntg.mywords.util.OnLifecycleEvent
import com.ntg.mywords.util.orZero
import com.ntg.mywords.util.timber
import com.ntg.mywords.vm.CalendarViewModel
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.WordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.time.LocalDate


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val wordViewModel: WordViewModel by viewModels()
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    // Preferences DataStore
    private val Context.dataStore by preferencesDataStore(name = Constant.PREFERENCE_DATA_STORE_NAME)

    // Proto DataStore
    private val userPreferencesStore by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = SettingsSerializer
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {

                AppNavHost(
                    wordViewModel = wordViewModel,
                    calendarViewModel = calendarViewModel,
                    loginViewModel = loginViewModel
                ) { _, navDestination, _ ->
                    timber("onDestinationChangeListener ${navDestination.route}")
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                HandleLifecycle(calendarViewModel, wordViewModel)
            }
        }
    }
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

    when (events.value) {
        Lifecycle.Event.ON_START,
        Lifecycle.Event.ON_RESUME -> {
            LaunchedEffect(key1 = listId) {
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
        Lifecycle.Event.ON_STOP,
        Lifecycle.Event.ON_PAUSE -> {
            calendarViewModel.stopLastTime()

        }
        else -> {}
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        Greeting("Android")
    }
}

