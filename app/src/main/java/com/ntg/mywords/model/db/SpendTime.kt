package com.ntg.mywords.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class SpendTime(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: LocalDate? = null,
    val startUnix: Long? = null,
    var endUnix: Long? = null,
    val type: Int? = null
)
