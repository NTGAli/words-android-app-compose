package com.ntg.vocabs.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sounds(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val mp3: String,
    val word:String,
    val type: String,
    val pronunciation: String? = null,
)
