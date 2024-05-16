package com.ntg.vocabs.screens

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.model.SpendTimeType
import com.ntg.vocabs.model.components.CalendarUiModel
import com.ntg.vocabs.model.db.TimeSpent
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.*
import com.ntg.vocabs.util.*
import com.ntg.vocabs.vm.CalendarViewModel
import com.ntg.vocabs.vm.WordViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeScreen(
    navController: NavController,
    calendarViewModel: CalendarViewModel,
    wordViewModel: WordViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.time),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                navController,
                calendarViewModel = calendarViewModel,
                wordViewModel
            )

        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    calendarViewModel: CalendarViewModel,
    wordViewModel: WordViewModel
) {

    LazyColumn(Modifier.padding(paddingValues)) {

        item {
            TimeContentItem(
                calendarViewModel = calendarViewModel,
                spendTimeType = SpendTimeType.Learning.ordinal,
                wordViewModel
            )
        }

//        item {
//            TimeContentItem(
//                calendarViewModel = calendarViewModel,
//                spendTimeType = SpendTimeType.Revision.ordinal,
//                wordViewModel
//            )
//        }


    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TimeContentItem(
    calendarViewModel: CalendarViewModel,
    spendTimeType: Int,
    wordViewModel: WordViewModel
) {
    var list :Map<String?, List<TimeSpent>> = mapOf()
    val listId = wordViewModel.currentList().observeAsState(initial = null).value?.id
    if (listId != null){
        list = calendarViewModel.getValidTimesSpentBaseType(spendTimeType, listId).observeAsState().value.orEmpty().groupBy { it.date }
    }

    val dateTime = remember {
        mutableStateOf(LocalDate.now())
    }

//    list.forEach { it.isSelected = it.date == dateTime.value }

    var totalTime = 0L
    var totalTimeOfDate = 0L
    var learningDays = 0


    val timeOfDate =
        calendarViewModel.getDataFromDate(dateTime.value, spendTimeType, listId.orZero()).observeAsState().value

    val timeSpent =
        calendarViewModel.getValidTimesSpentBaseType(spendTimeType, listId.orZero()).observeAsState().value.orEmpty()
            .toMutableStateList()

    timeSpent.forEach {
        if (it.startUnix != null && it.endUnix != null) {
            totalTime += getSecBetweenTimestamps(it.startUnix.orDefault(), it.endUnix.orDefault())
        }
    }

    timeOfDate.orEmpty().forEach {
        if (it.startUnix != null && it.endUnix != null) {


            if (getSecBetweenTimestamps(
                    it.startUnix.orDefault(),
                    it.endUnix.orDefault()
                ) == 33752){

                timber("TTTTTTTTIIIMMMM ${it}")

            }

            totalTimeOfDate += getSecBetweenTimestamps(
                it.startUnix.orDefault(),
                it.endUnix.orDefault()
            )
        }
    }

    learningDays = timeSpent.distinctBy { it.date }.size



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
            text = if (spendTimeType == SpendTimeType.Learning.ordinal)
                stringResource(id = R.string.learning)
            else stringResource(id = R.string.revision),
            style = fontMedium14(MaterialTheme.colorScheme.primary)
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp),
            text = totalTime.formatTime(),
            style = fontMedium16(MaterialTheme.colorScheme.onBackground)
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp, start = 16.dp),
            text = stringResource(id = R.string.days_format, learningDays),
            style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Column {
            LazyRow(modifier = Modifier.padding(bottom = 24.dp)) {

                items(list.keys.toList()) { index ->
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val localDate = LocalDate.parse(index, formatter)
                    ContentItem(CalendarUiModel.Date(
                        date = localDate,
                        isSelected = dateTime.value == localDate,
                        isToday = false
                    )) {
//                            calendarViewModel.selectDate(it)
                        dateTime.value = it.date
                    }
                }

            }

//            timeOfDate.orEmpty().forEach {
//                if (it.startUnix != null && it.endUnix != null) {
//                    Text(
//                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
//                        text = stringResource(
//                            id = R.string.format_hyphen,
//                            it.startUnix.orDefault().unixTimeToClock(),
//                            it.endUnix.orDefault().unixTimeToClock()
//                        ),
//                        style = fontMedium12(MaterialTheme.colorScheme.onBackground)
//                    )
//                }
//
//            }

            Text(
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                text = stringResource(
                    id = R.string.total_format,
                    totalTimeOfDate.secondsToClock()
                ),
                style = fontBold12(MaterialTheme.colorScheme.onBackground)
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

        }

    }


}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentItem(date: CalendarUiModel.Date, onClick: (CalendarUiModel.Date) -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp),
        colors = CardDefaults.cardColors(
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

