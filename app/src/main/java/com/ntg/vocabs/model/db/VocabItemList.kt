package com.ntg.vocabs.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VocabItemList (
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    var title: String="",
    var language: String="",
    var isSelected: Boolean=false,
    var synced: Boolean? = false,
    var email: String?=null
    )