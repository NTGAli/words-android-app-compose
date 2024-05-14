package com.ntg.vocabs.screens

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.db.Sounds
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingScreen(
    navController: NavController,
    wordViewModel: WordViewModel
) {


    var word by remember {
        mutableStateOf("")
    }

    var correct by remember {
        mutableStateOf("")
    }

    var isCorrect by remember {
        mutableStateOf<Boolean?>(null)
    }

    var left by remember {
        mutableIntStateOf(0)
    }


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.writing),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() },
                endText = if (left != 0) stringResource(id = R.string.left_format, left) else null
            )
        },
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                wordViewModel,
                navController,
                isCorrect,
                onSize = {
                    left = it
                }
            ) { user, correctWord ->
                word = user
                correct = correctWord
                isCorrect = null
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
            ) {
                Divider(
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                CustomButton(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.check), size = ButtonSize.XL
                ) {
                    isCorrect = word == correct
                }
            }
        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    navController: NavController,
    isCorrect: Boolean?,
    onSize:(Int) -> Unit,
    onValue: (String, String) -> Unit
) {
    timber("ContentContentContentContentContent")

    val mp = MediaPlayer()

    val words = remember {
        mutableStateOf(listOf<Word>())
    }

    var userType by remember {
        mutableStateOf("")
    }

    var isPlayed by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val word = remember {
        mutableStateOf<Word?>(null)
    }

    val pronouns = remember {
        mutableStateOf<Sounds?>(null)
    }

    var pos by remember {
        mutableIntStateOf(-1)
    }

    val listId = wordViewModel.currentList().observeAsState(initial = null).value?.id

    if (listId != null && words.value.isEmpty()) {
        wordViewModel.randomWords(listId).observeAsState(initial = null).value.let {
            if (it != null && words.value.isEmpty()) {
                words.value = it
                pos = 0
            }
        }
    }

    if (pos != -1) {
        LaunchedEffect(key1 = pos, block = {
            word.value = words.value[pos]
        })
    }



    if (isCorrect != null) {
        BottomSheet(
            isCorrect.orFalse(),
            pronouns.value?.word.orEmpty()
        ) {
            if (pos < words.value.size - 1) {
                isPlayed = false
                pos++
                userType = ""
                onValue.invoke("", words.value[pos].word.orEmpty())
                onSize.invoke(words.value.size - pos)
            } else {
                navController.popBackStack()
            }
        }
    }


    val lifecycle = LocalLifecycleOwner.current
    if (words.value.isNotEmpty()){
        LaunchedEffect(key1 = word.value, block = {
            wordViewModel.findPronouns(
                word.value?.word.orEmpty().lowercase(),
                word.value?.type.orEmpty()
            )
                .observe(lifecycle) {
                    if (it != null) {
                        if (it.id != (pronouns.value?.id ?: -1)) {
                            pronouns.value = it
                        }
                    } else if (pos < words.value.size - 1) {
                        isPlayed = false
                        pos++
                        userType = ""
                        onValue.invoke("", words.value[pos].word.orEmpty())
                    } else {
                        navController.popBackStack()
                    }

                }
        })
    }


    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    mp.setAudioAttributes(
        AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
    )


    if (pronouns.value != null) {

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = {

                    if (isPlayed) {
                        mp.start()
                    } else if (!isLoading) {
                        isLoading = true
                        val headerMap: MutableMap<String, String> = HashMap()
                        headerMap["User-Agent"] = getRandomUserAgent()
                        headerMap["Connection"] = "keep-alive"
                        headerMap["Accept-Encoding"] = "gzip, deflate, br"
                        headerMap["Accept"] = "*/*"
                        headerMap["Cookie"] = "JSESSIONID=58AA5CDC72C31FC9025FEFA598337866"
                        headerMap["Host"] = "www.oxfordlearnersdictionaries.com"

                        scope.launch {
                            try {
                                mp.reset()
                                mp.setDataSource(
                                    context,
                                    Uri.parse(pronouns.value!!.mp3),
                                    headerMap
                                )
                                mp.prepareAsync()
                                mp.setOnPreparedListener {
                                    mp.start()
                                    isPlayed = true
                                    isLoading = false
                                }
                            } catch (e: IOException) {
                                timber("ERR ::: ${e.printStackTrace()}")
                            }
                        }
                    }


                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.icons8_speaker_1),
                        contentDescription = "SPEAKER",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable {
                            mp.start()
                        },
                    text = pronouns.value!!.pronunciation.orEmpty(),
                    style = fontMedium14(
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }


            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                value = userType, onValueChange = {
                    userType = it
                    onValue.invoke(it, pronouns.value!!.word)

                }, label = {
                    Text(
                        text = stringResource(id = R.string.write_here),
                        style = fontMedium14(MaterialTheme.colorScheme.outline)
                    )
                })


        }
    }

}


fun getRandomUserAgent(): String {
    val osVersions = listOf(
        "Windows NT 10.0",
        "Windows NT 6.1",
        "Macintosh; Intel Mac OS X 10_15_7",
        "Linux; Android 12",
        "Linux; Android 11"
    )
    val browserNames =
        listOf("Chrome", "Firefox", "Safari", "Opera", "Edge", "SamsungBrowser", "UCBrowser")
    val chromeVersions = (80..100).toList()
    val firefoxVersions = (80..100).toList()
    val safariVersions = (10..15).toList()
    val operaVersions = (50..70).toList()
    val edgeVersions = (90..100).toList()
    val samsungBrowserVersions = (8..20).toList()
    val ucBrowserVersions = (10..14).toList()

    val osVersion = osVersions.random()
    val browserName = browserNames.random()
    val browserVersion = when (browserName) {
        "Chrome" -> chromeVersions.random()
        "Firefox" -> firefoxVersions.random()
        "Safari" -> safariVersions.random()
        "Opera" -> operaVersions.random()
        "Edge" -> edgeVersions.random()
        "SamsungBrowser" -> samsungBrowserVersions.random()
        "UCBrowser" -> ucBrowserVersions.random()
        else -> 80 // Default version if browser is not recognized
    }

    val mozillaVersion = "Mozilla/5.0"
    val appleWebKitVersion = "AppleWebKit/537.36 (KHTML, like Gecko)"

    return "$mozillaVersion ($osVersion) $appleWebKitVersion $browserName/$browserVersion.0"
}