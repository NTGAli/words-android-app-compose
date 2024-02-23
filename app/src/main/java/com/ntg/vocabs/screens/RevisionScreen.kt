package com.ntg.vocabs.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.model.SpendTimeType
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.model.db.TimeSpent
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.ui.theme.*
import com.ntg.vocabs.util.OnLifecycleEvent
import com.ntg.vocabs.util.getStateRevision
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.vm.CalendarViewModel
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    calendarViewModel: CalendarViewModel
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.revision),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, wordViewModel, navController)

        }
    )

//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        HandleLifecycle(calendarViewModel, wordViewModel)
//    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    navController: NavController,
) {

    val listId = wordViewModel.currentList().observeAsState().value?.id
    var words =
        wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value.orEmpty().filter {
            getStateRevision(
                it.revisionCount,
                it.lastRevisionTime
            ) == 2 || getStateRevision(
                it.revisionCount,
                it.lastRevisionTime
            ) == 3
        }

    val rejectedList = remember {
        mutableStateListOf<Word>()
    }

    words = words.filterNot { it in rejectedList }

    if (words.isNotEmpty()) {
        val word = words.get(0)
        LazyColumn(modifier = Modifier.padding(paddingValues)) {

            item {
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 24.dp),
                    text = stringResource(id = R.string.do_you_remeber_this_word),
                    style = fontRegular14(
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = word.word.orEmpty(), style = fontMedium24(MaterialTheme.colorScheme.onSurface))
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = word.type.orEmpty(),
                            style = fontRegular14(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }


                    if (word.translation.orEmpty().isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = word.translation.orEmpty(),
                            style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = word.pronunciation.orEmpty(),
                        style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Text(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Start),
                        text = word.definition.orEmpty(),
                        style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                }

            }

            item {
                CustomButton(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    text = stringResource(R.string.yes),
                    style = ButtonStyle.Contained,
                    type = ButtonType.Success,
                    size = ButtonSize.MD
                ) {
                    word.revisionCount = word.revisionCount + 1
                    word.lastRevisionTime = System.currentTimeMillis()
                    wordViewModel.editWord(word.id, word)
                    rejectedList.add(word)
                }

                CustomButton(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    text = stringResource(R.string.no),
                    style = ButtonStyle.TextOnly,
                    type = ButtonType.Danger,
                    size = ButtonSize.MD
                ) {
                    rejectedList.add(word)
                }
            }

        }
    } else {
        rejectedList.clear()
    }
}