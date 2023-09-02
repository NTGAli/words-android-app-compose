package com.ntg.mywords.model.req

import com.ntg.mywords.model.db.TimeSpent
import com.ntg.mywords.model.db.VocabItemList
import com.ntg.mywords.model.db.Word

data class BackupUserData(
    val words: List<Word>?,
    val vocabList: List<VocabItemList>?,
    val totalTimeSpent: List<TimeSpent>?
)
