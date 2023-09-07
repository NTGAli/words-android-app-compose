package com.ntg.mywords.model

data class VocabsListWithCount(
    val id: Int?,
    val title: String?,
    var language: String?,
    var isSelected: Boolean?,
    val countOfTableTwoItems: Int? = null
)
