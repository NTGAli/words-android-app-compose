package com.ntg.vocabs.screens
import com.ntg.vocabs.R
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.model.components.AppbarItem
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.Primary200
import com.ntg.vocabs.util.getIconStateRevision
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentWordScreen(navController: NavController, wordViewModel: WordViewModel, loginViewModel: LoginViewModel) {

    val listId = wordViewModel.currentList().observeAsState().value?.id
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
                onQueryChange = { query ->
                    wordViewModel.searchOnRecentWords(query, listId.orZero())
                },
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, wordViewModel, loginViewModel, navController)

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
    loginViewModel: LoginViewModel,
    navController: NavController,
){

    val wordsList = wordViewModel.searchedRecentWord.observeAsState().value
    val isPurchased =
        loginViewModel.getUserData().collectAsState(initial = null).value?.isPurchased.orFalse()
    LazyColumn(modifier = Modifier.padding(paddingValues)) {

        items(wordsList.orEmpty()) { word ->

            val painter = getIconStateRevision(word.revisionCount, word.lastRevisionTime)

            SampleItem(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = word.word.toString(),
                id = word.id,
                painter = painter,
                isBookmarked = word.bookmarked.orFalse(),
                unavailableBackup = if (!isPurchased && wordsList.orEmpty().filter { !it.synced.orFalse() }.size < 50) !word.synced.orFalse() else false
            ) { title, id, _ ->
                navController.navigate(Screens.WordDetailScreen.name + "?wordId=$id")
            }
        }


    }

}