package com.ntg.vocabs.model.response


data class Messages(
    val messages: List<RecentMessage>? = null,
    val full_screen_ad: FullScreenAd? = null
)

data class RecentMessage(
    val id: Int? = null,
    val title: String? = null,
    val message: String? = null,
    val action: String? = null,
    val link: String? = null,
)

data class FullScreenAd(
    val id: String?=null,
    val icon: String?=null,
    val title: String?=null,
    val description: String?=null,
    val discount: String?=null,
    val images: List<String>?=null,
    val btn_text: String?=null,
    val preview: Boolean? = null,
    val link: String?=null,
)

