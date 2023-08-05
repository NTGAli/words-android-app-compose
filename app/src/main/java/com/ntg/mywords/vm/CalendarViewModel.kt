package com.ntg.mywords.vm

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ntg.mywords.db.dao.WordDao
import com.ntg.mywords.model.CalendarDataSource
import com.ntg.mywords.model.components.CalendarUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val wordDao: WordDao
) : ViewModel() {

    private val dataSource = CalendarDataSource()

    // get CalendarUiModel from CalendarDataSource, and the lastSelectedDate is Today.
    private val calendarUiModel = dataSource.getData(lastSelectedDate = dataSource.today)

    var finalData: MutableLiveData<List<CalendarUiModel.Date>> = MutableLiveData(calendarUiModel.visibleDates)

    fun selectDate(date: CalendarUiModel.Date) {
        calendarUiModel.visibleDates.forEach {
            it.isSelected = it.date == date.date
        }
        finalData.value = listOf()
        finalData.value = calendarUiModel.visibleDates
    }


}