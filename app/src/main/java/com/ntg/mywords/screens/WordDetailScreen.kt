package com.ntg.mywords.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.model.components.*
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.*
import com.ntg.mywords.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(navController: NavController, wordViewModel: WordViewModel, wordId: Int?) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val word = wordViewModel.findWord(wordId)?.observeAsState()

//    if (word == null)
//        navController.popBackStack()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            setupAppbar(
                navController = navController,
                title = word?.value?.word.orEmpty(),
                scrollBehavior,
                wordId ?: -1,
                wordViewModel,
                word?.value
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, word?.value)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun setupAppbar(
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
        },
        popupItemOnClick = {
            if (it == 1) {
                navController.navigate(Screens.AddEditScreen.name + "?wordId=$wordId")
            } else {
                openBottomSheet = true
            }
        }
    )


    if (openBottomSheet){
        ModalBottomSheet(onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState) {

            Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
                Text(text = "Are you sure you want to delete this word?", style = fontMedium14(
                    MaterialTheme.colorScheme.onBackground))

                Row(modifier = Modifier.padding(top = 16.dp)) {
                    CustomButton(modifier = Modifier.weight(1f).padding(end = 4.dp), text = "no", type = ButtonType.Secondary, style = ButtonStyle.Outline){
                        openBottomSheet = false
                    }
                    CustomButton(modifier = Modifier.weight(1f).padding(start = 4.dp), text = "yes", type = ButtonType.Danger){
                        wordViewModel.deleteWord(word ?: Word())
                        openBottomSheet = false
                        navController.popBackStack()
                    }
                }
            }


        }
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, word: Word?) {

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
                Text(text = word?.word.orEmpty(), style = fontMedium24(MaterialTheme.colorScheme.onBackground))
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
                word?.verbForms?.pastParticiple.orEmpty().isNotEmpty()) {

                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary)
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
                                MaterialTheme.colorScheme.primaryContainer
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
                    IconButton(onClick = {
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.icons8_speaker_1),
                            contentDescription = "SPEAKER",
                            tint = Primary500
                        )
                    }
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = word.pronunciation,
                        style = fontMedium14(
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        item {
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                text = word?.definition.orEmpty(),
                style = fontRegular14(
                    MaterialTheme.colorScheme.onBackground
                )
            )

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