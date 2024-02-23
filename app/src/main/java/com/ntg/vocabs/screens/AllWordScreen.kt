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
import androidx.paging.compose.collectAsLazyPagingItems
import com.ntg.vocabs.R
import com.ntg.vocabs.components.EmptyWidget
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWordScreen(
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

    val words = wordViewModel.englishWords(query).collectAsLazyPagingItems()


    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 16.dp),content = {

        items(words.itemCount){
            SampleItem(
                title = words[it]?.word.orEmpty(),
                secondaryText = words[it]?.type,
                id = words[it]?.id,
            ) { _, id, _ ->
                navController.navigate(Screens.OnlineWordDetailsScreen.name + "?word=${words[it]?.word}&type=${words[it]?.type}")
            }
        }

        if (words.itemCount == 0) {
            item {
                EmptyWidget(
                    modifier = Modifier.padding(top = 24.dp),
                    title = stringResource(id = R.string.no_word)
                )
            }
        }

    })

//    LazyColumn(modifier = Modifier
//        .padding(paddingValues)
//        .padding(horizontal = 16.dp), content = {
//
//        items(words.value.orEmpty().filter { it.type != "null" }) { word ->
//            SampleItem(
//                title = word.word,
//                secondaryText = word.type,
//                id = word.id,
//            ) { _, id, _ ->
//                navController.navigate(Screens.OnlineWordDetailsScreen.name + "?word=${word.word}&type=${word.type}")
//            }
//        }
//
//        if (words.value != null && words.value.orEmpty().isEmpty()) {
//            item {
//                EmptyWidget(
//                    modifier = Modifier.padding(top = 24.dp),
//                    title = stringResource(id = R.string.no_word)
//                )
//            }
//        }
//
//    })

}
