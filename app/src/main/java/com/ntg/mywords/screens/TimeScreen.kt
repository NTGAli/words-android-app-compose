package com.ntg.mywords.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
import com.ntg.mywords.ui.theme.*
import com.ntg.mywords.util.*
import com.ntg.mywords.vm.CalendarViewModel
import com.ntg.mywords.vm.WordViewModel
import java.time.LocalDate
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
                title = stringResource(R.string.time),
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

    val dateTime = remember {
        mutableStateOf(LocalDate.now())
    }

    var totalTime = 0L
    var learningDays = 0

    timber("laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa ${calendarViewModel.getDataFromDate(dateTime.value).observeAsState().value}")

    val timeSpent = wordViewModel.getAllValidTimeSpent().observeAsState().value.orEmpty().toMutableStateList()

    timeSpent.forEach {
        if (it.startUnix != null && it.endUnix != null){
            totalTime += getSecBetweenTimestamps(it.startUnix.orDefault(), it.endUnix.orDefault())
        }
    }

    learningDays = timeSpent.distinctBy { it.date }.size


    LazyColumn(modifier = Modifier.padding(paddingValues)){

        item {
            var visible by remember {
                mutableStateOf(false)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(width = 2.dp, color = if (visible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp))

                    .clickable {
                        visible = !visible
                    }
            ) {
                Text(modifier = Modifier.padding(top = 16.dp, start = 16.dp), text = stringResource(id = R.string.learning), style = fontMedium14(MaterialTheme.colorScheme.primary))
                Text(modifier = Modifier.padding(top = 8.dp, start = 16.dp), text = totalTime.formatTime(), style = fontMedium16(MaterialTheme.colorScheme.onBackground))
                Text(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp, start = 16.dp), text = stringResource(id = R.string.days_format, learningDays), style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant))


                AnimatedVisibility(visible = visible) {

                    LazyRow(modifier = Modifier.padding(bottom = 16.dp)) {

                        items(list){date ->
                            ContentItem(date){
                                timber("akwjdlkjawlkdjlawkjdlkwjadlkjwalkdjlakwjdlwkjd ")
                                calendarViewModel.selectDate(it)
                                dateTime.value = it.date
                            }
                        }

                    }

                }

            }

        }

    }



}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentItem(date: CalendarUiModel.Date, onClick:(CalendarUiModel.Date) -> Unit) {
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
                MaterialTheme.colorScheme.surfaceVariant
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
                style = MaterialTheme.typography.bodySmall,
                color = if (date.isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = date.date.dayOfMonth.toString(), // date "15", "16"
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

