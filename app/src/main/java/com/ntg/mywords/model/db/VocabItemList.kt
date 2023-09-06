package com.ntg.mywords.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VocabItemList (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var title: String,
    var language: String,
    var isSelected: Boolean,
        )