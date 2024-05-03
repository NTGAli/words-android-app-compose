package com.ntg.vocabs.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.vocabs.util.orFalse

@Entity
data class VocabItemList (
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val fid: String?= null,
    var title: String="",
    var language: String="",
    var isSelected: Boolean=false,
    var synced: Boolean? = false,
    var isDeleted: Boolean? = false,
    var email: String?=null
    )

fun VocabItemList.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    map["id"] = id
    if (fid != null) map["fid"] = fid
    map["title"] = title
    map["language"] = language
    map["isSelected"] = isSelected
    if (synced != null) map["synced"] = synced.orFalse()
    if (isDeleted != null) map["isDeleted"] = isDeleted.orFalse()
    if (email != null) map["email"] = email.orEmpty()
    return map
}