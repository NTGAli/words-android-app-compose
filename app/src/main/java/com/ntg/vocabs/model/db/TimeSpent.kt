package com.ntg.vocabs.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.vocabs.util.orDefault
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orZero

@Entity
data class TimeSpent(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val fid: String? = null,
    val listId: Int? = null,
    val date: String? = null,
    val startUnix: Long? = null,
    var endUnix: Long? = null,
    val type: Int? = null,
    var synced: Boolean? = false,
    var isDeleted: Boolean? = false,
    var email: String?=null
    )


fun TimeSpent.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    map["id"] = id
    if (fid != null) map["fid"] = fid
    if (fid != null) map["listId"] = listId.orZero()
    if (fid != null) map["date"] = date.orEmpty()
    if (fid != null) map["startUnix"] = startUnix.orDefault()
    if (fid != null) map["endUnix"] = endUnix.orDefault()
    if (fid != null) map["type"] = type.orZero()
    if (fid != null) map["synced"] = synced.orFalse()
    if (fid != null) map["isDeleted"] = isDeleted.orFalse()
    if (fid != null) map["email"] = email.orEmpty()
    return map
}