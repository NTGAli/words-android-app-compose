package com.ntg.vocabs.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ntg.vocabs.model.data.GermanPronouns

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