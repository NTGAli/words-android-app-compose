package com.ntg.mywords.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val listId:Int=0,
    val word: String? = null,
    val translation: String? = null,
    val type: String? = null,
    val verbForms: VerbForms? = null,
    val pronunciation: String? = null,
    val definition: String? = null,
    val audio: String? = null,
    val example: List<String>? = null,
    val dateCreated: Long? = null,
    var revisionCount: Int = 0,
    var lastRevisionTime: Long? = null,
        )

data class VerbForms(
    val pastSimple: String? = null,
    val pastParticiple: String? = null
)