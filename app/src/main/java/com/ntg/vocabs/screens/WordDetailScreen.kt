package com.ntg.vocabs.screens

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.SelectableImage
import com.ntg.vocabs.model.components.*
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.playback.AndroidAudioPlayer
import com.ntg.vocabs.ui.theme.*
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.util.toPronunciation
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(navController: NavController, wordViewModel: WordViewModel, wordId: Int?) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val word = wordViewModel.findWord(wordId)?.observeAsState()

//    if (word == null)
//        navController.popBackStack()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SetupAppbar(
                navController = navController,
                title = word?.value?.word.orEmpty(),
                scrollBehavior,
                wordId ?: -1,
                wordViewModel,
                word?.value
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, word?.value,navController)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupAppbar(
    navController: NavController,
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    wordId: Int,
    wordViewModel: WordViewModel,
    word: Word?
) {
    var openBottomSheet by remember {
        mutableStateOf(false)
    }
    val skipPartiallyExpanded by remember { mutableStateOf(true) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    Appbar(
        title = title,
        enableNavigation = true,
        scrollBehavior = scrollBehavior,
        navigationOnClick = { navController.popBackStack() },
        popupItems = listOf(
            PopupItem(
                id = 1,
                title = "edit",
                icon = painterResource(id = R.drawable.edit_16_1_5)
            ),
            PopupItem(
                id = 2,
                title = "remove",
                icon = painterResource(id = R.drawable.trash_16_1_5)
            )
        ),
        actionOnClick = {
            wordViewModel.isBookmarked(!word?.bookmarked.orFalse(), wordId)
        },
        popupItemOnClick = {
            if (it == 1) {
                navController.navigate(Screens.AddEditScreen.name + "?wordId=$wordId")
            } else if (it == 2) {
                openBottomSheet = true
            }
        },
        actions = listOf(
            AppbarItem(
                0,
                if (word?.bookmarked.orFalse()) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder
            )
        )
    )


    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState
        ) {

            Column(
                Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)) {
                Text(
                    text = "Are you sure you want to delete this word?", style = fontMedium14(
                        MaterialTheme.colorScheme.onBackground
                    )
                )

                Row(modifier = Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    CustomButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        text = "no",
                        type = ButtonType.Secondary,
                        style = ButtonStyle.Outline
                    ) {
                        openBottomSheet = false
                    }
                    CustomButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        text = "yes",
                        type = ButtonType.Danger
                    ) {
                        timber("DDDDDDDDDDDDDDDD :::: $word")
                        if (word != null){
                            timber("DDDDDDDDDDDDDDDD :::: 111")
                            if (word.fid != null){
                                timber("DDDDDDDDDDDDDDDD :::: 222 ---- ${word.id}")
                                wordViewModel.deleteWord(word.id)
                            }else{
                                wordViewModel.deleteWord(word)
                            }
                        }
                        openBottomSheet = false
                        navController.popBackStack()
                    }
                }
            }


        }
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, word: Word?,navController: NavController) {

    val ctx = LocalContext.current
    val player by lazy {
        AndroidAudioPlayer(ctx)
    }
    val scope = rememberCoroutineScope()

    var visible by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
    ) {

        item {
            Row(
                modifier = Modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word?.word.orEmpty(),
                    style = fontMedium24(MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = word?.type.orEmpty(),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        item {
            if (word?.translation.orEmpty().isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = word?.translation.orEmpty(),
                    style = fontMedium16(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

        }

        item {
            if (word?.verbForms?.pastSimple.orEmpty().isNotEmpty() &&
                word?.verbForms?.pastParticiple.orEmpty().isNotEmpty()
            ) {

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            visible = !visible
                        }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(start = 8.dp),
                        text = stringResource(id = R.string.verb_forms),
                        style = fontMedium14(
                            MaterialTheme.colorScheme.onSurface
                        )
                    )

                    AnimatedVisibility(visible = visible) {

                        Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                            if (word?.verbForms?.pastSimple != null) {
                                TextWithContext(
                                    title = stringResource(id = R.string.past_simple),
                                    description = word.verbForms.pastSimple
                                )
                            }

                            if (word?.verbForms?.pastParticiple != null) {
                                TextWithContext(
                                    modifier = Modifier.padding(top = 8.dp),
                                    title = stringResource(id = R.string.past_participle),
                                    description = word.verbForms.pastParticiple
                                )
                            }
                        }

                    }

                }
            }
        }

        item {
            if (word?.pronunciation != null) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (word.voice.orEmpty().isNotEmpty()){
                        IconButton(onClick = {
                            val audioFile = File(word.voice!!)
                            if (player.isPlaying()) return@IconButton
                            player.playFile(audioFile ?: return@IconButton)
//                            timber("voice-file-path ${audioFile?.absoluteFile}")
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.icons8_speaker_1),
                                contentDescription = "SPEAKER",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }


                    if (word.sound.orEmpty().isNotEmpty()){
                        IconButton(onClick = {
                            val mp = MediaPlayer()
                            try {
                                scope.launch {
                                    mp.setDataSource(word.sound)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        mp.prepare()
                                    }
                                    mp.setOnPreparedListener {
                                        mp.start()
                                    }
                                }
                            } catch (e: IOException) {
                                timber("ERR ::: ${e.printStackTrace()}")
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.icons8_speaker_1),
                                contentDescription = "SPEAKER",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.padding(start = 16.dp).padding(bottom = 8.dp),
                        text = word.pronunciation.toPronunciation(),
                        style = fontMedium14(
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        item{
            if (word?.images.orEmpty().isNotEmpty()){
                Row {
                    SelectableImage(modifier = Modifier.weight(1f),path = word?.images!!.first(), onClick = {
                        navController.navigate(Screens.FullScreenImageScreen.name+"?path=$it")
                    })

                    Spacer(modifier = Modifier.weight(4f))
                }
            }
        }

        if (word?.definition.orEmpty().isNotEmpty()){
            item {
                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    text = word?.definition.orEmpty(),
                    style = fontRegular14(
                        MaterialTheme.colorScheme.onBackground
                    )
                )

            }
        }

        if (word?.synonyms.orEmpty().isNotEmpty()){
            item {
                Text(modifier = Modifier.padding(top = 8.dp), text = stringResource(id = R.string.synonyms_format, word?.synonyms.toString().drop(1).dropLast(1)), style = fontRegular14(MaterialTheme.colorScheme.onBackground))
            }
        }

        if (word?.antonyms.orEmpty().isNotEmpty()){
            item {
                Text(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),text = stringResource(id = R.string.antonyms_format, word?.antonyms.toString().drop(1).dropLast(1)),style = fontRegular14(MaterialTheme.colorScheme.onBackground))
            }
        }

        items(word?.example.orEmpty()) {
            Text(
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                text = it,
                style = fontRegular14(MaterialTheme.colorScheme.onBackground)
            )
        }

    }


}