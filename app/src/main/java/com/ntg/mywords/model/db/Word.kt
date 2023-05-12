package com.ntg.mywords.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String? = null,
    val type: String? = null,
    val pronunciation: String? = null,
    val definition: String? = null,
    val audio: String? = null,
    val example: List<String>? = null,
    val dateCreated: Long? = null,
    val revisionCount: Int = 0,
    val lsatRevisionTime: Long? = null


        )