package com.ntg.vocabs.db.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateConverter {

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromString(value: String?): LocalDate? {
        return try {
            value?.let {
                LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
            }
        }catch (e: Exception) {
            null
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toString(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}