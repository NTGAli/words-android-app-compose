package com.ntg.vocabs.model

data class VocabsListWithCount(
    val id: Int?,
    val title: String?,
    var language: String?,
    var isSelected: Boolean?,
    val countOfTableTwoItems: Int? = null
)
