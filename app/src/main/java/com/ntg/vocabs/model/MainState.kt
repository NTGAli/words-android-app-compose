package com.ntg.vocabs.model

import com.ntg.vocabs.util.backup.DriveFileInfo


data class MainState(
    val email:String? = null,
    val restoreFiles:List<DriveFileInfo> = emptyList()
)