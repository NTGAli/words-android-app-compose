package com.ntg.mywords.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.ntg.mywords.model.components.CalendarUiModel
import com.ntg.mywords.util.timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import java.util.stream.Stream

class CalendarDataSource {

    val today: LocalDate
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            return LocalDate.now()
        }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(): CalendarUiModel {

        val currentDate = LocalDate.now()
        val tenDaysAgo = currentDate.minusDays(365)

        val list = arrayListOf<CalendarUiModel.Date>()

        var currentDateIterator = currentDate
        while (!currentDateIterator.isBefore(tenDaysAgo)) {
            list.add(CalendarUiModel.Date(currentDateIterator, isSelected = currentDateIterator == currentDate, isToday = currentDateIterator == currentDate))
            currentDateIterator = currentDateIterator.minusDays(1)
        }
        return CalendarUiModel(CalendarUiModel.Date(LocalDate.now(), true, isToday = true), list)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val numOfDays = ChronoUnit.DAYS.between(endDate, startDate)
        return Stream.iterate(startDate) { date ->
            date.minusDays( 1)
        }
            .limit(numOfDays)
            .collect(Collectors.toList())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toUiModel(
        dateList: List<LocalDate>,
        lastSelectedDate: LocalDate
    ): CalendarUiModel {
        return CalendarUiModel(
            selectedDate = toItemUiModel(lastSelectedDate, true),
            visibleDates = dateList.map {
                toItemUiModel(it, it.isEqual(lastSelectedDate))
            },
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toItemUiModel(date: LocalDate, isSelectedDate: Boolean) = CalendarUiModel.Date(
        isSelected = isSelectedDate,
        isToday = date.isEqual(today),
        date = date,
    )
}