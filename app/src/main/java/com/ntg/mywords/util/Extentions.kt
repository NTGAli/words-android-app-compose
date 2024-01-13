package com.ntg.mywords.util

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.CountDownTimer
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.ntg.mywords.R
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.model.Failure
import com.ntg.mywords.model.Result
import com.ntg.mywords.model.Success
import com.ntg.mywords.util.Constant.DATA_STORE_FILE_NAME
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.time.Duration.Companion.seconds

fun Float?.orZero() = this ?: 0f
fun Long?.orDefault() = this ?: 0L
fun String?.orDefault() = this ?: ""
fun Int?.orZero() = this ?: 0
fun Boolean?.orFalse() = this ?: false
fun Boolean?.orTrue() = this ?: true

fun timber(msg: String) {
    Timber.d(msg)
}

fun timber(title: String, msg: String) {
    Timber.d("$title ----------> $msg")
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.openInBrowser(url: String){
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    this.startActivity(browserIntent)
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

fun String.hasEnoughDigits() = count(Char::isDigit) > 0

fun Char?.ifNotNull(): String? = this?.toString()

fun Context.checkInternet(): Boolean {
    return (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null
}

fun enoughDigitsForPass(
    str: String,
    errorMsg: String
) = if (str.count(Char::isDigit) > 0) {
    Success(str)
} else {
    Failure(errorMsg)
}

fun longEnoughForPass(
    str: String,
    errorMsg: String
) = if (str.length > 4) {
    Success(str)
} else {
    Failure(errorMsg)
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

fun validEmail(email: String): Result<String> =
    if (email.matches(Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")))
        Success(email)
    else Failure("invalid email!")

fun String.validEmail() = this.matches(Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"))

fun notFalse(
    value: Boolean?,
    errorMessage: String = "error!"
): Result<String> =
    if (value.orFalse()) Success(value.toString())
    else Failure(errorMessage)


fun String?.notEqual(str: String) = this != str


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
fun CountDownTimer(start: Int, onTick: (Int) -> Unit) {
    var time = start
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            onTick.invoke(time--)
        }
    }
}


fun Int.minutesToTimeFormat(): String {
    val hours = this / 60
    val remainingMinutes = this % 60

    // Format hours and minutes with leading zeros if needed
    val formattedHours = hours.toString().padStart(2, '0')
    val formattedMinutes = remainingMinutes.toString().padStart(2, '0')

    return "$formattedHours:$formattedMinutes"
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
        this < day -> "${this / hour} hours"
        this < month -> "${this / day} days"
        this < year -> "${this / month} months"
        else -> "${this / year} years"
    }
}

fun Long.unixTimeToReadable(): String {
    val date = Date(this * 1000L)
    val dateFormat = SimpleDateFormat("dd MMM yyyy")
    return dateFormat.format(date)
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


fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())
    getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic), start, end)
            }
            is UnderlineSpan -> addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
            is ForegroundColorSpan -> addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
        }
    }
}


fun Context.sendMail(to: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "vnd.android.cursor.item/email" // or "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        this.toast(this.getString(R.string.sth_wrong))
    } catch (t: Throwable) {
        this.toast(this.getString(R.string.sth_wrong))
    }
}

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiToBeCalled: suspend () -> Response<T>
): LiveData<NetworkResult<T>> {


    return liveData(dispatcher) {

        var response: Response<T>? = null
        try {
            emit(NetworkResult.Loading())
            timber("TREE_RES_DATE ::: SF1 $response")

            response = apiToBeCalled.invoke()

            timber("TREE_RES_DATE ::: SF $response")

            if (response.isSuccessful) {
                emit(NetworkResult.Success(data = response.body()))
            } else {
                emit(
                    NetworkResult.Error(message = response.errorBody().toString())
                )

            }
        } catch (e: HttpException) {
            emit(NetworkResult.Error(message = "HttpException ::: ${e.message}"))
        } catch (e: IOException) {
            emit(NetworkResult.Error(message = "IOException ::: ${e.message} --- ${e.printStackTrace()}"))
        } catch (e: Exception) {
            emit(NetworkResult.Error(message = "Exception ::: ${e.message}"))
        }
    }
}
