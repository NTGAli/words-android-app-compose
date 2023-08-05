package com.ntg.mywords.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.model.CalendarDataSource
import com.ntg.mywords.model.components.AppbarItem
import com.ntg.mywords.model.components.CalendarUiModel
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.Primary200
import com.ntg.mywords.util.OnLifecycleEvent
import com.ntg.mywords.util.timber
import com.ntg.mywords.vm.CalendarViewModel
import com.ntg.mywords.vm.WordViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeScreen(navController: NavController, wordViewModel: WordViewModel, calendarViewModel: CalendarViewModel) {

    val numberOfAllWords = wordViewModel.getMyWords().observeAsState().value.orEmpty().size
    val enableSearchBar = remember { mutableStateOf(false) }


    wordViewModel.searchOnRecentWords("")

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
                    wordViewModel.searchOnRecentWords(query)
                },
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->



//            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
//                Header(data = calendarUiModel)
                Content(paddingValues = innerPadding, wordViewModel, navController, calendarViewModel = calendarViewModel)

//            }


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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Content(paddingValues: PaddingValues, wordViewModel: WordViewModel, navController: NavController, calendarViewModel: CalendarViewModel){
    val list = calendarViewModel.finalData.observeAsState().value.orEmpty()
    timber("laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")



    LazyRow(modifier = Modifier.padding(paddingValues)) {

        items(list){date ->
            ContentItem(date){
                timber("akwjdlkjawlkdjlawkjdlkwjadlkjwalkdjlakwjdlwkjd ")
                calendarViewModel.selectDate(it)
            }
        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentItem(date: CalendarUiModel.Date, onClick:(CalendarUiModel.Date) -> Unit) {
    timber("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM ${date.isSelected}")
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
        ,
        colors = CardDefaults.cardColors(
            // background colors of the selected date
            // and the non-selected date are different
            containerColor = if (date.isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        ),
        onClick = {
            date.isSelected = true
            onClick.invoke(date)
        }
    ) {
        Column(
            modifier = Modifier
                .width(40.dp)
                .height(48.dp)
                .padding(4.dp)
        ) {
            Text(
                text = date.day, // day "Mon", "Tue"
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = date.date.dayOfMonth.toString(), // date "15", "16"
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

