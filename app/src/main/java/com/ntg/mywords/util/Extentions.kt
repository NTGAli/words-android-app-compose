package com.ntg.mywords.util

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.ntg.mywords.R
import com.ntg.mywords.model.Failure
import com.ntg.mywords.model.Result
import com.ntg.mywords.model.Success
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

fun Float?.orZero() = this ?: 0f
fun Long?.orDefault() = this ?: 0L
fun Boolean?.orFalse() = this?: false

fun timber(msg: String) {
    Timber.d(msg)
}

fun timber(title: String, msg: String) {
    Timber.d("$title ----------> $msg")
}

fun Context.toast(msg: String){
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


fun Long.calculateRevisionStatus(numberOfRevision: Int){

    val todayUnix = System.currentTimeMillis()

//    if ()

}

fun getDaysBetweenTimestamps(startTimeStamp: Long, endTimeStamp: Long): Int {
    val startDate = Date(startTimeStamp)
    val endDate = Date(endTimeStamp)

    val differenceMillis = endDate.time - startDate.time

    return TimeUnit.MILLISECONDS.toDays(differenceMillis).toInt()
}

@Composable
fun getStateRevision(revisionCount: Int, lsatRevisionTime: Long?): Painter {

    val diffTime = getDaysBetweenTimestamps(lsatRevisionTime.orDefault(),System.currentTimeMillis())


    return when(revisionCount){

        0 -> {
            when(diffTime){

                in 0..1 -> {
                    painterResource(id = R.drawable.chart_full)
                }

                2 ->{
                    painterResource(id = R.drawable.chart_medium)
                }

                else -> {
                    painterResource(id = R.drawable.chart_low)
                }

            }
        }

        1 -> {
            when(diffTime){

                in 1..5 -> {
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
            when (diffTime){

                in 1..10 ->{
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

            when(diffTime){

                in 1 .. (revisionCount * 7) ->{
                    painterResource(id = R.drawable.chart_full)
                }

                in (revisionCount *7) .. (revisionCount + 10) -> {
                    painterResource(id = R.drawable.chart_medium)
                }

                else -> {
                    painterResource(id = R.drawable.chart_low)
                }

            }

        }
    }
}