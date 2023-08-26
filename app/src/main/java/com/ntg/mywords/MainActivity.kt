package com.ntg.mywords

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import com.ntg.mywords.model.SpendTimeType
import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.nav.AppNavHost
import com.ntg.mywords.ui.theme.AppTheme
import com.ntg.mywords.util.OnLifecycleEvent
import com.ntg.mywords.util.timber
import com.ntg.mywords.vm.CalendarViewModel
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.WordViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val wordViewModel: WordViewModel by viewModels()
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

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
                HandleLifecycle(calendarViewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HandleLifecycle(calendarViewModel: CalendarViewModel) {
    OnLifecycleEvent { owner, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                calendarViewModel.insertSpendTime(
                    TimeSpent(
                        id = 0,
                        date = LocalDate.now(),
                        startUnix = System.currentTimeMillis(),
                        endUnix = null,
                        type = SpendTimeType.Learning.ordinal
                    )
                )
            }
//            Lifecycle.Event.ON_DESTROY,
            Lifecycle.Event.ON_STOP -> {
                calendarViewModel.stopLastTime()
            }

            else -> {}
        }
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

