package com.ntg.vocabs.model.response

data class EntryMetadata(
    val id: String?,
    val offensive: Boolean?,
    val section: String?,
    val sort: String?,
    val src: String?,
    val stems: List<String?>?,
    val uuid: String?
)