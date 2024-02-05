package com.ntg.vocabs.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.ntg.vocabs.model.db.VerbForms

class VerbFormsConverter {
    @TypeConverter
    fun verbFormsToString(verbForms: VerbForms?): String? = Gson().toJson(verbForms)

    @TypeConverter
    fun stringToVerbForms(string: String?): VerbForms? = Gson().fromJson(string, VerbForms::class.java)
}