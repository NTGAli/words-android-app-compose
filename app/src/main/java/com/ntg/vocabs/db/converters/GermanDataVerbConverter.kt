package com.ntg.vocabs.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.ntg.vocabs.model.data.GermanDataVerb

class GermanDataVerbConverter {
    @TypeConverter
    fun germanVerbToString(verbForms: GermanDataVerb?): String? = Gson().toJson(verbForms)

    @TypeConverter
    fun stringToVerbForms(string: String?): GermanDataVerb? = Gson().fromJson(string, GermanDataVerb::class.java)
}