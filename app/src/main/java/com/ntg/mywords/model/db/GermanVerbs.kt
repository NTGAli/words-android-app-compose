package com.ntg.mywords.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.mywords.model.data.GermanDataVerb

@Entity
data class GermanVerbs(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val word: String,
    val data: GermanDataVerb,
)
