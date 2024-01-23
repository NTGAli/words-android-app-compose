package com.ntg.mywords.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GermanNouns(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val lemma: String,
    val genus: String,
    val plural: String,
)
