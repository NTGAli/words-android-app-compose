package com.ntg.vocabs.util

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
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
import com.ntg.vocabs.services.AlarmReceiver
import com.ntg.vocabs.services.ReminderService
import com.ntg.vocabs.ui.theme.Danger500
import com.ntg.vocabs.ui.theme.Success500
import com.ntg.vocabs.ui.theme.Warning500
import com.ntg.vocabs.util.Constant.MAX_SIZE_IMAGE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
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
    } catch (e: Exception) {
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

                0 -> {
                    painterResource(id = R.drawable.full_pie)
                }

                in 1..2 -> {
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

                in 0..11 -> {
                    painterResource(id = R.drawable.full_pie)
                }

                in 12..15 -> {
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


@Composable
fun getColorRevision(revisionCount: Int, lsatRevisionTime: Long?): Modifier {

    val diffTime =
        getDaysBetweenTimestamps(lsatRevisionTime.orDefault(), System.currentTimeMillis())



    val color =  when(revisionCount) {

        0 -> {
            when (diffTime) {

                0 -> {
                    Success500
                }

                in 1..2 -> {
                    Warning500
                }

                else -> {
                    Danger500
                }

            }
        }

        1 -> {
            when (diffTime) {

                in 0..5 -> {
                    Success500
                }

                in 6..11 -> {
                    Warning500
                }

                else -> {
                    Danger500
                }

            }
        }

        2 -> {
            when (diffTime) {

                in 0..11 -> {
                    Success500
                }

                in 12..15 -> {
                    Warning500
                }

                else -> {
                    Danger500
                }
            }
        }

        else -> {

            when (diffTime) {

                in 0..(revisionCount * 7) -> {
                    Success500
                }

                in (revisionCount * 7)..(revisionCount + 10) -> {
                    Warning500
                }

                else -> {
                    Danger500
                }

            }

        }
    }

    return Modifier.size(8.dp).clip(CircleShape).background(color)
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

                in 0..11 -> {
                    1
                }

                in 12..15 -> {
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

fun nextRevisionDay(revisionCount: Int): Int{

    return when (revisionCount) {

        0 -> {
            1
        }

        1 -> {
            6
        }

        2 -> {
            12
        }

        else -> {
            (revisionCount * 7)
        }
    }
}

fun setReviewNotification(
    context: Context,
    word: String, dayStart: Int
) {


//    val data = Data.Builder()
//        .putString("word", word)
//        .putInt("id", generateUniqueFiveDigitId())
//        .build()
//
//    val workRequest = OneTimeWorkRequest.Builder(ReviewWorker::class.java)
//        .setInputData(data)
//        .setInitialDelay(16, TimeUnit.MINUTES)
//        .addTag(word)
//        .build()


//    WorkManager.getInstance(context)
//        .enqueueUniqueWork(
//            "reviewWorker_$word",
//            ExistingWorkPolicy.APPEND_OR_REPLACE,
//            workRequest
//        )

//    WorkManager.getInstance(context).enqueue(workRequest)
}

fun getNextNDaysUnix(n: Int): Long {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val now = LocalDateTime.now()
        LocalDateTime.now()
        val futureDate = now.plusDays(n.toLong())
        val futureInstant = futureDate.toInstant(ZoneOffset.UTC)
        futureInstant.epochSecond
    } else {
        val currentTimeMillis = System.currentTimeMillis()
        val futureTimeMillis = currentTimeMillis + TimeUnit.DAYS.toMillis(n.toLong())
        futureTimeMillis / 1000
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

fun getFormattedTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
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

fun getFirstDayOfWeek(): Int {
    val locale = Locale.getDefault() // Get the user's default locale
    val calendar = Calendar.getInstance(locale) // Create a Calendar instance with the user's locale
    return calendar.firstDayOfWeek // Return the first day of the week
}

// Function to convert number to ordinal string
fun ordinal(number: Int): String {
    return when (number % 100) {
        11, 12, 13 -> "${number}th"
        else -> when (number % 10) {
            1 -> "${number}st"
            2 -> "${number}nd"
            3 -> "${number}rd"
            else -> "${number}th"
        }
    }
}

fun getStartOfWeek(): Long {
    val locale = Locale.getDefault() // Get the user's default locale

    val calendar = Calendar.getInstance(locale)
    // Set to the start of the week based on locale
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun getEndOfWeek(): Long {
    val calendar = Calendar.getInstance()
    // Set to the start of the week based on locale
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    // Add 7 days to get to the end of the week
    calendar.add(Calendar.DAY_OF_WEEK, 7)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun dayToName(firstDayOfWeek: Int): String {
//    val firstDayOfWeek = getFirstDayOfWeek()
    return when (firstDayOfWeek) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> "Unknown"
    }
//    println("First day of the week: $dayName")
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
    while (this.rowBytes * this.height > MAX_SIZE_IMAGE * 1024 && quality > 0) {
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

fun showToastMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun getFormattedDateInString(timeInMillis: Long, format: String): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(timeInMillis)
}

fun getFormattedDate(timeInString: String, format: String): Date {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.parse(timeInString)
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

fun setRemainderAlarm(word: String, type: String,id: Int, context: Context) {
    val myCalendar = nextTenMinutes()
//    myCalendar.set(Calendar.HOUR_OF_DAY, 10)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val reminderService = ReminderService()
    val reminderReceiverIntent = Intent(context, AlarmReceiver::class.java)

    reminderReceiverIntent.putExtra("word", word)
    reminderReceiverIntent.putExtra("type", type)
    reminderReceiverIntent.putExtra("id", id)
    reminderReceiverIntent.putExtra("isServiceRunning", isServiceRunning(reminderService, context))
    val pendingIntent =
        PendingIntent.getBroadcast(context, id, reminderReceiverIntent,
            PendingIntent.FLAG_IMMUTABLE)
    val formattedDate = getFormattedDateInString(myCalendar.timeInMillis, "dd/MM/YYYY HH:mm")
    timber("TimeSetInMillis:", "$formattedDate --- $id")

    alarmManager.setAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP, myCalendar.timeInMillis, pendingIntent
    )
}

fun nextTenMinutes(): Calendar {
    val myCalendar = Calendar.getInstance()
    myCalendar.add(Calendar.MINUTE, 6)
    timber("nextTenMinutes ::::: $myCalendar")
    return myCalendar
}

@Suppress("DEPRECATION")
fun isServiceRunning(reminderService: ReminderService, context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (reminderService.javaClass.name == service.service.className) {
            Log.i("isMyServiceRunning?", true.toString() + "")
            return true
        }
    }
    Log.i("isMyServiceRunning?", false.toString() + "")
    return false
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
