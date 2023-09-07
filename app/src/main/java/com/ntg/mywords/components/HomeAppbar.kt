package com.ntg.mywords.components

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import com.ntg.mywords.R
import com.ntg.mywords.ui.theme.fontMedium12
import com.ntg.mywords.ui.theme.fontRegular12
import com.ntg.mywords.util.ifNotNull
import com.ntg.mywords.util.timber
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppbar(
    title: String?,
    searchCallback: () -> Unit,
    notificationCallback: () -> Unit,
    profileCallback: () -> Unit,
    voiceSearch: (String) -> Unit
) {


    var openVoiceSearch by remember {
        mutableStateOf(false)
    }

    OpenVoiceSearch(openVoiceSearch) {
        if (it != null){
            voiceSearch.invoke(it)
        }
        openVoiceSearch = false
    }

    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        searchCallback.invoke()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                        .padding(start = 16.dp),
                    text = "search words",
                    style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
                )

                IconButton(onClick = {
                    openVoiceSearch = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.microphone_02),
                        contentDescription = "mic"
                    )
                }
            }
        },
        actions = {


            IconButton(onClick = {
                notificationCallback.invoke()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.bell_02),
                    contentDescription = "notifications"
                )
            }


            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        profileCallback.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier,
                    text = try {
                        title?.first().ifNotNull() ?: ":)"
                    } catch (e: Exception) {
                        ":)"
                    },
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = fontMedium12(MaterialTheme.colorScheme.onPrimary)
                )
//                Image(painter = painterResource(id = R.drawable.icon_britain)
//                    , contentDescription = "flag")
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    )
}

@Composable
fun OpenVoiceSearch(launch:Boolean,voiceSearch: (String?) -> Unit) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                it.data
                val res: ArrayList<String> =
                    it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
                voiceSearch.invoke(res[0])
            }
        }
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE,
        Locale.getDefault()
    )
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

    if (launch){
        try {
            launcher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    voiceSearch.invoke(null)

}