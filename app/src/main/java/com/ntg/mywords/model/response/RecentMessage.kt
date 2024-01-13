package com.ntg.mywords.model.response

data class RecentMessage(
    val id: Int,
    val title: String,
    val description: String,
    val button: String,
    val link: String,
    val date: String,
)
