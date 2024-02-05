package com.ntg.vocabs.model.req

import com.ntg.vocabs.model.db.TimeSpent
import com.ntg.vocabs.model.db.VocabItemList
import com.ntg.vocabs.model.db.Word

data class BackupUserData(
    val words: List<Word>?,
    val vocabList: List<VocabItemList>?,
    val totalTimeSpent: List<TimeSpent>?
)
