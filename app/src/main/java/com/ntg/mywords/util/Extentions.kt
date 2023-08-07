package com.ntg.mywords.util

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.ntg.mywords.R
import com.ntg.mywords.model.Failure
import com.ntg.mywords.model.Result
import com.ntg.mywords.model.Success
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Float?.orZero() = this ?: 0f
fun Long?.orDefault() = this ?: 0L
fun Int?.orZero() = this ?: 0
fun Boolean?.orFalse() = this ?: false

fun timber(msg: String) {
    Timber.d(msg)
}

fun timber(title: String, msg: String) {
    Timber.d("$title ----------> $msg")
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun notEmptyOrNull(
    value: String?,
    errorMessage: String = "error!"
): Result<String> =
    if (value != "" && value != null) {
        Success(value)
    } else {
        Failure(errorMessage)
    }

fun notFalse(
    value: Boolean?,
    errorMessage: String = "error!"
): Result<String> =
    if (value.orFalse()) Success(value.toString())
    else Failure(errorMessage)


fun Long.calculateRevisionStatus(numberOfRevision: Int) {

    val todayUnix = System.currentTimeMillis()

//    if ()

}

fun getDaysBetweenTimestamps(startTimeStamp: Long, endTimeStamp: Long): Int {
    val startDate = Date(startTimeStamp)
    val endDate = Date(endTimeStamp)

    val differenceMillis = endDate.time - startDate.time

    return TimeUnit.MILLISECONDS.toDays(differenceMillis).toInt()
}

fun getSecBetweenTimestamps(startTimeStamp: Long, endTimeStamp: Long): Int {
    val startDate = Date(startTimeStamp)
    val endDate = Date(endTimeStamp)

    val differenceMillis = endDate.time - startDate.time

    return TimeUnit.MILLISECONDS.toSeconds(differenceMillis).toInt()
}

@Composable
fun getIconStateRevision(revisionCount: Int, lsatRevisionTime: Long?): Painter {

    val diffTime =
        getDaysBetweenTimestamps(lsatRevisionTime.orDefault(), System.currentTimeMillis())


    return when (revisionCount) {

        0 -> {
            when (diffTime) {

                in 0..1 -> {
                    painterResource(id = R.drawable.chart_full)
                }

                2 -> {
                    painterResource(id = R.drawable.chart_medium)
                }

                else -> {
                    painterResource(id = R.drawable.chart_low)
                }

            }
        }

        1 -> {
            when (diffTime) {

                in 0..5 -> {
                    painterResource(id = R.drawable.chart_full)
                }

                in 6..11 -> {
                    painterResource(id = R.drawable.chart_medium)
                }

                else -> {
                    painterResource(id = R.drawable.chart_low)
                }

            }
        }

        2 -> {
            when (diffTime) {

                in 0..10 -> {
                    painterResource(id = R.drawable.chart_full)
                }

                in 10..15 -> {
                    painterResource(id = R.drawable.chart_medium)
                }

                else -> {
                    painterResource(id = R.drawable.chart_low)
                }
            }
        }

        else -> {

            when (diffTime) {

                in 0..(revisionCount * 7) -> {
                    painterResource(id = R.drawable.chart_full)
                }

                in (revisionCount * 7)..(revisionCount + 10) -> {
                    painterResource(id = R.drawable.chart_medium)
                }

                else -> {
                    painterResource(id = R.drawable.chart_low)
                }

            }

        }
    }
}

fun getStateRevision(revisionCount: Int, lsatRevisionTime: Long?): Int {

    val diffTime =
        getDaysBetweenTimestamps(lsatRevisionTime.orDefault(), System.currentTimeMillis())


    return when (revisionCount) {

        0 -> {
            when (diffTime) {

                in 0..1 -> {
                    1
                }

                2 -> {
                    2
                }

                else -> {
                    3
                }

            }
        }

        1 -> {
            when (diffTime) {

                in 0..5 -> {
                    1
                }

                in 6..11 -> {
                    2
                }

                else -> {
                    3
                }

            }
        }

        2 -> {
            when (diffTime) {

                in 0..10 -> {
                    1
                }

                in 10..15 -> {
                    2
                }

                else -> {
                    3
                }
            }
        }

        else -> {

            when (diffTime) {

                in 0..(revisionCount * 7) -> {
                    1
                }

                in (revisionCount * 7)..(revisionCount + 10) -> {
                    2
                }

                else -> {
                    3
                }

            }

        }
    }
}


fun Int.getUnixTimeNDaysAgo(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -this)

    val date = calendar.time
    return date.time
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

fun Long.formatTime(): String {
    val minute = 60L
    val hour = minute * 60
    val day = hour * 24
    val month = day * 30
    val year = month * 12

    return when {
        this < minute -> "$this seconds"
        this < hour -> "${this / minute} minutes"
        this < day -> "${this/ hour} hours"
        this < month -> "${this / day} days"
        this < year -> "${this / month} months"
        else -> "${this / year} years"
    }
}

fun Long.unixTimeToClock(): String {
    val date = Date(this)
    val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(date)
}

fun Long.secondsToClock(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60

    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}