package com.ntg.vocabs.screens
import com.ntg.vocabs.R
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.model.components.AppbarItem
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.util.getIconStateRevision
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(navController: NavController, wordViewModel: WordViewModel, openSearch: Boolean, query: String) {

    val listId = wordViewModel.currentList().observeAsState().value?.id
    val numberOfBookmarkedWords = wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value.orEmpty().filter { it.bookmarked.orFalse() }.size
    val enableSearchBar = remember { mutableStateOf(openSearch) }
    val userSearchVoiceQuery = remember { mutableStateOf(query) }

    wordViewModel.searchOnBookmarked(query, listId.orZero())

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.bookmarks_format,numberOfBookmarkedWords),
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
                searchQueryText = userSearchVoiceQuery,
                onQueryChange = { query ->
                    wordViewModel.searchOnBookmarked(query, listId.orZero())
                },
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, wordViewModel, navController)

        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    navController: NavController,
){

    val wordsList = wordViewModel.searchedWordOnBookmarked.observeAsState().value

    LazyColumn(modifier = Modifier.padding(paddingValues)) {

        items(wordsList.orEmpty()) { word ->

            val painter = getIconStateRevision(word.revisionCount, word.lastRevisionTime)

            SampleItem(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = word.word.toString(),
                id = word.id,
                painter = painter
            ) { _, id, _ ->
                navController.navigate(Screens.WordDetailScreen.name + "?wordId=$id")

            }
        }


    }

}