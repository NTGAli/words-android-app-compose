package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.EmptyWidget
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.util.getIconStateRevision
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourWordScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel,
    query: String
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(
                wordViewModel = wordViewModel,
                navController = navController,
                loginViewModel = loginViewModel,
                paddingValues = innerPadding,
                query = query
            )
        }
    )
}

@Composable
private fun Content(
    wordViewModel: WordViewModel,
    navController: NavController,
    loginViewModel: LoginViewModel,
    paddingValues: PaddingValues,
    query: String
) {
    val listId = wordViewModel.currentList().observeAsState().value?.id
    val userWords = wordViewModel.getWordsBaseListId(listId.orZero()).observeAsState().value

    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 16.dp), content = {

        items(userWords.orEmpty().filter { it.word.orEmpty().contains(query) }) { word ->
            val painter = getIconStateRevision(word.revisionCount, word.lastRevisionTime)

            val isPurchased =
                loginViewModel.getUserData().collectAsState(initial = null).value?.isPurchased.orFalse()

            SampleItem(
                title = word.word.toString(),
                id = word.id,
                painter = painter,
                isBookmarked = word.bookmarked.orFalse(),
                unavailableBackup = if (!isPurchased && userWords.orEmpty().filter { !it.synced.orFalse() }.size < 50) !word.synced.orFalse() else false
            ) { _, id, _ ->
                navController.navigate(Screens.WordDetailScreen.name + "?wordId=$id")
            }
        }

        if (userWords.orEmpty().isEmpty()) {
            item {
                EmptyWidget(
                    modifier = Modifier.padding(top = 24.dp),
                    title = stringResource(id = R.string.no_word)
                )
            }
        }

    })
}