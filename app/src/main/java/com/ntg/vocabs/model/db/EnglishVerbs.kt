package com.ntg.vocabs.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EnglishVerbs(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val word: String,
    val pastSimple: String,
    val pp: String,
    val ing: String
)
