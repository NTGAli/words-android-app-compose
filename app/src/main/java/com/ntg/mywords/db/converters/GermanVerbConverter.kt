package com.ntg.mywords.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.ntg.mywords.model.data.VerbGermanForm
import com.ntg.mywords.model.db.VerbForms

class GermanVerbConverter {
    @TypeConverter
    fun germanVerbFormsToString(verbForms: VerbGermanForm?): String? = Gson().toJson(verbForms)

    @TypeConverter
    fun stringToGermanVerbForms(string: String?): VerbGermanForm? = Gson().fromJson(string, VerbGermanForm::class.java)
}