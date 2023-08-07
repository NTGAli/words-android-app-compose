package com.ntg.mywords.vm

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.mywords.db.dao.TimeSpentDao
import com.ntg.mywords.model.CalendarDataSource
import com.ntg.mywords.model.components.CalendarUiModel
import com.ntg.mywords.model.db.TimeSpent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val timeSpentDao: TimeSpentDao
) : ViewModel() {



    // get CalendarUiModel from CalendarDataSource, and the lastSelectedDate is Today.
    private val calendarUiModel = CalendarDataSource().getData()

    private var lastItem: LiveData<TimeSpent> = MutableLiveData()
    private var listOfTime: LiveData<List<TimeSpent>> = MutableLiveData()
    var finalData: MutableLiveData<List<CalendarUiModel.Date>> =
        MutableLiveData(calendarUiModel.visibleDates)
    private var allValidLearningTimeSpent: LiveData<List<TimeSpent>> = MutableLiveData()
    private var allValidTimeSpent: LiveData<List<TimeSpent>> = MutableLiveData()




    fun insertSpendTime(spendTime: TimeSpent) {
        viewModelScope.launch {
            timeSpentDao.insert(spendTime)
        }
    }


    fun updateSpendTime(spendTime: TimeSpent?) {
        viewModelScope.launch {
            timeSpentDao.update(spendTime ?: TimeSpent(id = -1))
        }
    }

    fun getLastItem(): LiveData<TimeSpent> {
        viewModelScope.launch {
            lastItem = timeSpentDao.getLastItem()
        }
        return lastItem
    }

    fun stopLastTime() {
        viewModelScope.launch {
            timeSpentDao.stopTime(System.currentTimeMillis())
        }
    }

    fun selectDate(date: CalendarUiModel.Date) {
        calendarUiModel.visibleDates.forEach {
            it.isSelected = it.date == date.date
        }
        finalData.value = listOf()
        finalData.value = calendarUiModel.visibleDates
    }

    fun getDataFromDate(date: LocalDate, type: Int): LiveData<List<TimeSpent>> {
        viewModelScope.launch {
            listOfTime = timeSpentDao.getDtaOfDate(date, type)
        }
        return listOfTime
    }

    fun getValidTimesSpentBaseType(type: Int): LiveData<List<TimeSpent>> {
        viewModelScope.launch {
            allValidTimeSpent = timeSpentDao.getAllValidTimesBaseType(type)
        }
        return allValidTimeSpent
    }

    fun getAllValidLearningTimeSpent(): LiveData<List<TimeSpent>> {
        viewModelScope.launch {
            allValidLearningTimeSpent = timeSpentDao.getAllValidLearningTime()
        }
        return allValidLearningTimeSpent
    }




}