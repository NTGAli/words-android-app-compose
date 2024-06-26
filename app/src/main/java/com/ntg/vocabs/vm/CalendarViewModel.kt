package com.ntg.vocabs.vm

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.vocabs.db.dao.TimeSpentDao
import com.ntg.vocabs.model.CalendarDataSource
import com.ntg.vocabs.model.SpendTimeType
import com.ntg.vocabs.model.components.CalendarUiModel
import com.ntg.vocabs.model.db.TimeSpent
import com.ntg.vocabs.util.generateUniqueFiveDigitId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

//@RequiresApi(Build.VERSION_CODES.O)
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
    var currentScreen: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertSpendTime(type: SpendTimeType, listId: Int, start: Long) {

//            removeNullTime()
        viewModelScope.launch {
            val spendTime = TimeSpent(
                id = generateUniqueFiveDigitId(),
                listId = listId,
                date = LocalDate.now().toString(),
                startUnix = start,
                endUnix = System.currentTimeMillis(),
                type = type.ordinal
            )

            timeSpentDao.insert(spendTime)

//            stopLastTimeOffset(
//                timeSpentDao.insert(spendTime).toInt()
//            )
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

    fun removeNullTime() = viewModelScope.launch { timeSpentDao.removeNullTime() }

    private fun stopLastTimeOffset(insertedId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            timeSpentDao.stopTime(System.currentTimeMillis(), insertedId)
        }
    }

    fun stopLastTime() {
        viewModelScope.launch(Dispatchers.IO) {
            timeSpentDao.stopLast(System.currentTimeMillis())
        }
    }

    fun selectDate(date: CalendarUiModel.Date) {
        calendarUiModel.visibleDates.forEach {
            it.isSelected = it.date == date.date
        }
        finalData.value = listOf()
        finalData.value = calendarUiModel.visibleDates
    }

    fun getDataFromDate(date: LocalDate, type: Int, listId: Int): LiveData<List<TimeSpent>> {
        viewModelScope.launch {
            listOfTime = timeSpentDao.getDtaOfDate(date, type, listId)
        }
        return listOfTime
    }

    fun getValidTimesSpentBaseType(type: Int, listId: Int): LiveData<List<TimeSpent>> {
        viewModelScope.launch {
            allValidTimeSpent = timeSpentDao.getAllValidTimesBaseType(type, listId)
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