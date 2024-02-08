package com.ntg.vocabs.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DriveBackup(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val isSuccess: Boolean,
    val time: String,
    val description: String
)
