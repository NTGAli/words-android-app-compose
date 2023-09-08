package com.ntg.mywords.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class TimeSpent(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val listId: Int? = null,
    val date: String? = null,
    val startUnix: Long? = null,
    var endUnix: Long? = null,
    val type: Int? = null,
    var synced: Boolean? = null,

    )
