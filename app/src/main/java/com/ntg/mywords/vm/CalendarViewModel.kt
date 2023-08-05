package com.ntg.mywords.vm

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.mywords.db.dao.SpendTimeDao
import com.ntg.mywords.db.dao.WordDao
import com.ntg.mywords.model.CalendarDataSource
import com.ntg.mywords.model.components.CalendarUiModel
import com.ntg.mywords.model.db.SpendTime
import com.ntg.mywords.model.db.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val spendTimeDao: SpendTimeDao
) : ViewModel() {


    private val dataSource = CalendarDataSource()

    // get CalendarUiModel from CalendarDataSource, and the lastSelectedDate is Today.
    private val calendarUiModel = dataSource.getData(lastSelectedDate = dataSource.today)

    private var lastItem: LiveData<SpendTime> = MutableLiveData()
    var finalData: MutableLiveData<List<CalendarUiModel.Date>> =
        MutableLiveData(calendarUiModel.visibleDates)


    fun insertSpendTime(spendTime: SpendTime) {
        viewModelScope.launch {
            spendTimeDao.insert(spendTime)
        }
    }

    fun updateSpendTime(spendTime: SpendTime?) {
        viewModelScope.launch {
            spendTimeDao.update(spendTime ?: SpendTime(id = -1))
        }
    }

    fun getLastItem(): LiveData<SpendTime> {
        viewModelScope.launch {
            lastItem = spendTimeDao.getLastItem()
        }
        return lastItem
    }

    fun stopLastTime() {
        viewModelScope.launch {
            spendTimeDao.stopTime(System.currentTimeMillis())
        }
    }

    fun selectDate(date: CalendarUiModel.Date) {
        calendarUiModel.visibleDates.forEach {
            it.isSelected = it.date == date.date
        }
        finalData.value = listOf()
        finalData.value = calendarUiModel.visibleDates
    }


}