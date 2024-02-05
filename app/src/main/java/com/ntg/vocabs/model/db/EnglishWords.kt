package com.ntg.vocabs.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EnglishWords(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val word: String,
    val type: String,
)
