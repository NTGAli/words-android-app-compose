package com.ntg.vocabs.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ntg.vocabs.R
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.model.Failure
import com.ntg.vocabs.model.Result
import com.ntg.vocabs.model.Success
import com.ntg.vocabs.util.Constant.MAX_SIZE_IMAGE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream
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

fun Context.toast(resId: Int) {
    Toast.makeText(this, this.getString(resId), Toast.LENGTH_SHORT).show()
}

fun Context.openInBrowser(url: String) {
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

fun String.validEmail() =
    this.trim().matches(Regex("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"))

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

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(inputDate: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val date = LocalDate.parse(inputDate, formatter)

        val year = date.year
        val month = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
//    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        val dayOfMonth = date.dayOfMonth

        "$year $month $dayOfMonth"
    }catch (e: Exception){
        "Do you want to restore it?"
    }
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

fun generateUniqueFiveDigitId(): Int {
    val timestamp = System.currentTimeMillis()
    val input = "$timestamp".toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(input)
    val hash = digest.fold(0) { acc, byte -> (acc shl 8) + byte.toInt() }
    return hash and 0x7FFFFFFF % 90000 + 10000 // Ensures 5-digit ID
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

fun getSubdirectory(audio: String): String {
    return when {
        audio.startsWith("bix") -> "bix"
        audio.startsWith("gg") -> "gg"
        audio.firstOrNull()?.isDigit() == true || audio.firstOrNull()
            ?.isWhitespace() == true || audio.firstOrNull() in setOf(
            '_',
            '@',
            '#',
            '$',
            '%',
            '^',
            '&',
            '*'
        ) -> "number"

        else -> audio.firstOrNull()?.toString() ?: "default"
    }
}

fun getCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    return currentDate.format(formatter)
}

fun unzip(zipFilePath: String, destinationDir: String): Boolean {
    val buffer = ByteArray(1024)

    try {
        // Create input stream from the zip file
        val zipInputStream = ZipInputStream(FileInputStream(zipFilePath))

        // Create the destination directory if it doesn't exist
        val destDir = File(destinationDir)
        if (!destDir.exists()) {
            destDir.mkdir()
        }

        // Iterate through each entry in the zip file
        var zipEntry = zipInputStream.nextEntry
        while (zipEntry != null) {
            val newFile = File(destinationDir + File.separator + zipEntry.name)

            // Create parent directories if they don't exist
            newFile.parent?.let { File(it).mkdirs() }

            // Write the current entry to the destination file
            val fos = newFile.outputStream()
            var len: Int
            while (zipInputStream.read(buffer).also { len = it } > 0) {
                fos.write(buffer, 0, len)
            }
            fos.close()

            // Move to the next entry
            zipEntry = zipInputStream.nextEntry
        }

        // Close the zip input stream
        zipInputStream.closeEntry()
        zipInputStream.close()
        File(zipFilePath).delete()

        println("Unzip completed successfully.")
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun String.toPronunciation(): String {
    if (this.isEmpty()) return ""
    var finalPronouns = this
    if (!this.startsWith('/')) {
        finalPronouns = "/$finalPronouns"
    }
    if (!this.endsWith('/')) {
        finalPronouns = "$finalPronouns/"
    }

    return finalPronouns
}

@Composable
fun getIconStateRevision(revisionCount: Int, lsatRevisionTime: Long?): Painter {

    val diffTime =
        getDaysBetweenTimestamps(lsatRevisionTime.orDefault(), System.currentTimeMillis())


    return when (revisionCount) {

        0 -> {
            when (diffTime) {

                in 0..1 -> {
                    painterResource(id = R.drawable.full_pie)
                }

                2 -> {
                    painterResource(id = R.drawable.medium_pie)
                }

                else -> {
                    painterResource(id = R.drawable.low_pie)
                }

            }
        }

        1 -> {
            when (diffTime) {

                in 0..5 -> {
                    painterResource(id = R.drawable.full_pie)
                }

                in 6..11 -> {
                    painterResource(id = R.drawable.medium_pie)
                }

                else -> {
                    painterResource(id = R.drawable.low_pie)
                }

            }
        }

        2 -> {
            when (diffTime) {

                in 0..10 -> {
                    painterResource(id = R.drawable.full_pie)
                }

                in 10..15 -> {
                    painterResource(id = R.drawable.medium_pie)
                }

                else -> {
                    painterResource(id = R.drawable.low_pie)
                }
            }
        }

        else -> {

            when (diffTime) {

                in 0..(revisionCount * 7) -> {
                    painterResource(id = R.drawable.full_pie)
                }

                in (revisionCount * 7)..(revisionCount + 10) -> {
                    painterResource(id = R.drawable.medium_pie)
                }

                else -> {
                    painterResource(id = R.drawable.low_pie)
                }

            }

        }
    }
}

fun generateCode(): Int {
    val random = kotlin.random.Random
    val fiveDigitNumber = random.nextInt(10000, 100000)
    return fiveDigitNumber
}

fun getStateRevision(revisionCount: Int, lsatRevisionTime: Long?): Int {

    val diffTime =
        getDaysBetweenTimestamps(lsatRevisionTime.orDefault(), System.currentTimeMillis())


    return when (revisionCount) {

        0 -> {
            when (diffTime) {

                0 -> {
                    1
                }

                in 1..2 -> {
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
    val dateFormat = SimpleDateFormat("EEE MMM dd yyyy")
    val date = Date(this)
    return dateFormat.format(date)
}


fun Long.unixTimeToClock(): String {
    val date = Date(this)
    val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return format.format(date)
}

fun Long.secondsToClock(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60

    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}


fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val activeNetwork =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}


fun Bitmap.compressBitmap(fos: FileOutputStream): Bitmap? {
    var quality = 100
    timber("SIIIIIIIIIIIIIIIIII ${this.rowBytes * this.height}")
    while (this.rowBytes * this.height > MAX_SIZE_IMAGE * 1024 && quality > 0){
        this.compress(Bitmap.CompressFormat.JPEG, quality, fos)
        quality -= 5
    }

    return this
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
                Typeface.BOLD_ITALIC -> addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ), start, end
                )
            }

            is UnderlineSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.Underline),
                start,
                end
            )

            is ForegroundColorSpan -> addStyle(
                SpanStyle(color = Color(span.foregroundColor)),
                start,
                end
            )
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
