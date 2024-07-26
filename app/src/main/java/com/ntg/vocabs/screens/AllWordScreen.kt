package com.ntg.vocabs.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.SampleItem
import com.ntg.vocabs.components.SearchBox
import com.ntg.vocabs.model.PaginationState
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.fontMedium16
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.vm.WordViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWordScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    q: String,
    showSearchbar: Boolean = false
) {

    val query = remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = q) {
        query.value = q
    }

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background.copy(alpha = 0.2f)
        ), start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (showSearchbar){
                Column(
                    modifier = Modifier
                        .background(gradientBrush)
                        .clickable(enabled = true, onClick = {
                            coroutineScope.launch {
                                focusRequester.requestFocus()
                            }
                        }, indication = null, interactionSource = MutableInteractionSource())
                ) {
                    SearchBox(
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp), query,focusRequester
                    )
                }
            }
        }, content = { innerPadding ->
        Content(
            navController = navController,
            paddingValues = innerPadding,
            wordViewModel = wordViewModel,
            query = query.value
        )
    })
}

@Composable
private fun Content(
    navController: NavController,
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    query: String
) {

    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val words = wordViewModel.notesList.collectAsStateWithLifecycle()
    val pagingState = wordViewModel.pagingState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = query) {
        wordViewModel.clearPaging()
        wordViewModel.englishWords(query)
    }

    val shouldPaginate = remember {
        derivedStateOf {
            (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -5) >= (lazyColumnListState.layoutInfo.totalItemsCount - 3)
        }
    }

    wordViewModel.canPaginate = shouldPaginate.value

    LaunchedEffect(key1 = shouldPaginate.value) {
        if (shouldPaginate.value && pagingState.value == PaginationState.REQUEST_INACTIVE) {
            wordViewModel.englishWords(query)
        }
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        state = lazyColumnListState,
        content = {


            item {
                Spacer(modifier = Modifier.padding(paddingValues))
            }

            if (pagingState.value == PaginationState.EMPTY) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        text = stringResource(id = R.string.not_found_question),
                        style = fontMedium16(MaterialTheme.colorScheme.onSurfaceVariant),
                        textAlign = TextAlign.Center
                    )
                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.insert_manully),
                        style = ButtonStyle.TextOnly
                    ) {
                        navController.navigate(Screens.AddEditScreen.name)
                    }
                }

                coroutineScope.launch {
                    lazyColumnListState.animateScrollToItem(0)
                }
            }

            items(words.value.size) {
                timber("WORD_SIZE ::: ${words.value.size} --- $it")
                if (words.value.size > it) {
                    SampleItem(
                        title = words.value[it].word,
                        secondaryText = words.value[it].type,
                        id = words.value[it].id,
                    ) { _, id, _ ->
                        navController.navigate(Screens.OnlineWordDetailsScreen.name + "?word=${words.value[it]?.word}&type=${words.value[it]?.type}")
                    }
                }
            }


            if (pagingState.value == PaginationState.LOADING || pagingState.value == PaginationState.PAGINATING) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .progressSemantics()
                            .size(24.dp),
                        color = MaterialTheme.colorScheme.error,
                        strokeWidth = 2.dp
                    )
                }
            }
        })
}
