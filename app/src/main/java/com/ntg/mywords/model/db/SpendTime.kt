package com.ntg.mywords.model.db

import androidx.room.Entity
import java.time.LocalDate

@Entity
data class SpendTime(
    val id: Int,
    val date: LocalDate? = null,
    val startUnix: Long? = null,
    val endUnix: Long? = null,
    val type: Int? = null
)
