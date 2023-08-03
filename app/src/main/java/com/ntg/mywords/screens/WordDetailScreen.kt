package com.ntg.mywords.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.model.components.AppbarItem
import com.ntg.mywords.model.components.PopupItem
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
                wordId ?: -1
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
    wordId: Int
) {

    Appbar(
        title = title,
        enableNavigation = true,
        scrollBehavior = scrollBehavior,
        navigationOnClick = { navController.popBackStack() },
        actions = listOf(
            AppbarItem(
                id = 1,
                imageVector = ImageVector.vectorResource(id = R.drawable.menu_16_2),
                iconColor = Secondary500
            )
        ),
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

            }
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, word: Word?) {

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
                Text(text = word?.word.orEmpty(), style = fontMedium24(Secondary900))
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = word?.type.orEmpty(),
                    style = fontMedium14(Secondary500)
                )
            }
        }

        item {
            if (word?.translation.orEmpty().isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = word?.translation.orEmpty(),
                    style = fontMedium16(Secondary600)
                )
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
                            Secondary800
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
                    Secondary900
                )
            )

        }

        items(word?.example.orEmpty()) {
            Text(
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                text = it,
                style = fontRegular14(Secondary700)
            )
        }

    }


}