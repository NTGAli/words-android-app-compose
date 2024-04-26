package com.ntg.vocabs.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimeSpent(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val listId: Int? = null,
    val date: String? = null,
    val startUnix: Long? = null,
    var endUnix: Long? = null,
    val type: Int? = null,
    var synced: Boolean? = false,
    var email: String?=null
    )
