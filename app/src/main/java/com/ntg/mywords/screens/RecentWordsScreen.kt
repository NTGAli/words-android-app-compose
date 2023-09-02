package com.ntg.mywords.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.SampleItem
import com.ntg.mywords.model.components.AppbarItem
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.Primary200
import com.ntg.mywords.util.getIconStateRevision
import com.ntg.mywords.util.orZero
import com.ntg.mywords.util.timber
import com.ntg.mywords.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentWordScreen(navController: NavController, wordViewModel: WordViewModel) {

    val listId = wordViewModel.getIdOfListSelected().observeAsState().value?.id
    val numberOfAllWords = wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value.orEmpty().size
    val enableSearchBar = remember { mutableStateOf(false) }


    wordViewModel.searchOnRecentWords("", listId.orZero())

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.recent_words_format,numberOfAllWords),
                scrollBehavior = scrollBehavior,
                actions = listOf(
                    AppbarItem(
                        id = 0,
                        imageVector = Icons.Rounded.Search
                    )
                ),
                actionOnClick = {
                    enableSearchBar.value = true
                },
                enableSearchbar = enableSearchBar,
                onQueryChange = {query ->
                    wordViewModel.searchOnRecentWords(query, listId.orZero())
                },
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, wordViewModel, navController)

        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.AddEditScreen.name)
                },
                containerColor = Primary200
            ) {
                Icon(imageVector = Icons.Rounded.Add, tint = Color.Black, contentDescription = "FL")
            }
        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    navController: NavController,
){

    val wordsList = wordViewModel.searchedRecentWord.observeAsState().value

    LazyColumn(modifier = Modifier.padding(paddingValues)) {

        items(wordsList.orEmpty()) { word ->

            val painter = getIconStateRevision(word.revisionCount, word.lastRevisionTime)

            SampleItem(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = word.word.toString(),
                id = word.id,
                painter = painter
            ) { title, id, _ ->

                timber("kawljdlkajwdlkjawlkdj $id")

                navController.navigate(Screens.WordDetailScreen.name + "?wordId=$id")
//                navController.navigate(Screens.AddEditScreen.name+"?wordId=$id")

            }
        }


    }

}