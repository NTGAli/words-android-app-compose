package com.ntg.vocabs.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.ntg.vocabs.model.data.VerbGermanForm

class GermanVerbConverter {
    @TypeConverter
    fun germanVerbFormsToString(verbForms: VerbGermanForm?): String? = Gson().toJson(verbForms)

    @TypeConverter
    fun stringToGermanVerbForms(string: String?): VerbGermanForm? = Gson().fromJson(string, VerbGermanForm::class.java)
}