package com.ntg.vocabs.screens


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllGermanVerbsScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    query: String
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(
                navController = navController,
                paddingValues = innerPadding,
                wordViewModel = wordViewModel,
                query = query
            )
        }
    )
}

@Composable
private fun Content(
    navController: NavController,
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    query: String
) {

//    val words = wordViewModel.getBooks().observeAsState().value
//    val pagingBooksss: PagingData<GermanVerbs> by wordViewModel.allGermanVerbs.collectAsLazyPagingItems()
//
//
//    val pagingBooks: LazyPagingItems<GermanVerbs> = remember(wordViewModel.allGermanVerbs) {
//        wordViewModel.allGermanVerbs
//    }

    val words = wordViewModel.allGermanVerbs(query).observeAsState(initial = null).value


    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 16.dp), content = {

        items(words.orEmpty()) { word ->
            SampleItem(
                title = word.word,
//                secondaryText = word.,
//                id = word?.id,
            ) { _, id, _ ->
                navController.navigate(Screens.VerbsFormScreen.name+"?verb=${word.word}&form=${word.word}")
            }
        }

        if (words.orEmpty().isEmpty() && words != null) {
            item {
                EmptyWidget(
                    modifier = Modifier.padding(top = 24.dp),
                    title = stringResource(id = R.string.no_word)
                )
            }
        }

    })

}
