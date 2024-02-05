package com.ntg.vocabs.db.converters

import androidx.room.TypeConverter
import com.ntg.vocabs.util.Constant.SEPARATOR

class ExamplesConverters {

    @TypeConverter
    fun toString(value: List<String>?): String? {
        return value?.joinToString(separator = SEPARATOR) { it }
    }

    @TypeConverter
    fun toList(date: String?): List<String>? {
        return date?.split(SEPARATOR)?.map { it }
    }
}