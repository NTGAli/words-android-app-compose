package com.ntg.mywords.model.req

import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.model.db.Word

data class BackupUserData(
    val words: List<Word>?,
    val totalTimeSpent: List<TimeSpent>?
)
