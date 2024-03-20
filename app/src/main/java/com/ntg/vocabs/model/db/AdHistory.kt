package com.ntg.vocabs.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AdHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val aid: String,
    val date: String,
    val isSkipped: Boolean,
)
