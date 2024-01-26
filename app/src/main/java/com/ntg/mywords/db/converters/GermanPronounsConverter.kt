package com.ntg.mywords.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ntg.mywords.model.data.GermanPronouns
import com.ntg.mywords.util.Constant

class GermanPronounsConverter {
    @TypeConverter
    fun toString(value: List<GermanPronouns>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toList(date: String?): List<GermanPronouns>? {
        val listType = object : TypeToken<List<GermanPronouns>>() {}.type
        return Gson().fromJson(date, listType)
    }
}