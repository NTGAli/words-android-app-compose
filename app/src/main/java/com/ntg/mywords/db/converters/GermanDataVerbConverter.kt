package com.ntg.mywords.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.ntg.mywords.model.data.GermanDataVerb
import com.ntg.mywords.model.db.VerbForms

class GermanDataVerbConverter {
    @TypeConverter
    fun germanVerbToString(verbForms: GermanDataVerb?): String? = Gson().toJson(verbForms)

    @TypeConverter
    fun stringToVerbForms(string: String?): GermanDataVerb? = Gson().fromJson(string, GermanDataVerb::class.java)
}