package com.ntg.vocabs.model.db

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
    val article: String? = null,
    val plural: String? = null,
    val verbForms: VerbForms? = null,
    val pronunciation: String? = null,
    val definition: String? = null,
    val audio: String? = null,
    var example: List<String>? = null,
    val synonyms: List<String>? = null,
    val antonyms: List<String>? = null,
    val dateCreated: Long? = null,
    var revisionCount: Int = 0,
    var lastRevisionTime: Long? = null,
    var voice: String? = null,
    var sound: String? = null,
    var images: List<String>? = null,
    var bookmarked: Boolean? = false,
    var synced: Boolean? = null,
        )

data class VerbForms(
    val pastSimple: String? = null,
    val pastParticiple: String? = null
)