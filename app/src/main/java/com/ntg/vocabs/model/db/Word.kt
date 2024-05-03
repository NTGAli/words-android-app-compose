package com.ntg.vocabs.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.vocabs.util.orDefault
import com.ntg.vocabs.util.orFalse

@Entity
data class Word(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var fid: String?= null,
    val listId: Int = 0,
    val word: String? = null,
    val translation: String? = null,
    val type: String? = null,
    val article: String? = null,
    val plural: String? = null,
    val verbForms: VerbForms? = null,
    val pronunciation: String? = null,
    val definition: String? = null,
    val audio: String? = null,
    var example: List<String>? = null,
    val synonyms: List<String>? = null,
    val antonyms: List<String>? = null,
    val dateCreated: Long? = null,
    var revisionCount: Int = 0,
    var lastRevisionTime: Long? = null,
    var voice: String? = null,
    var sound: String? = null,
    var images: List<String>? = null,
    var bookmarked: Boolean? = false,
    var synced: Boolean? = false,
    var voiceSynced: Boolean? = null,
    var imageSynced: Boolean? = null,
    var email: String? = null,
    var isDeleted: Boolean?=false
)

data class VerbForms(
    val pastSimple: String? = null,
    val pastParticiple: String? = null
)


fun Word.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    map["id"] = id
    if (fid != null) map["fid"] = fid.orEmpty()
    map["listId"] = listId
    if (word != null) map["word"] = word
    if (translation != null) map["translation"] = translation
    if (type != null) map["type"] = type
    if (article != null) map["article"] = article
    if (plural != null) map["plural"] = plural
    if (verbForms != null) map["verbForms"] = verbForms.toMap()
    if (pronunciation != null) map["pronunciation"] = pronunciation
    if (definition != null) map["definition"] = definition
    if (audio != null) map["audio"] = audio
    if (example != null) map["example"] = example.orEmpty()
    if (antonyms != null) map["synonyms"] = synonyms.orEmpty()
    if (fid != null) map["antonyms"] = antonyms.orEmpty()
    if (dateCreated != null) map["dateCreated"] = dateCreated
    map["revisionCount"] = revisionCount
    if (lastRevisionTime != null) map["lastRevisionTime"] = lastRevisionTime.orDefault()
    if (voice != null) map["voice"] = voice.orEmpty()
    if (sound != null) map["sound"] = sound.orEmpty()
    if (images != null) map["images"] = images.orEmpty()
    if (bookmarked != null) map["bookmarked"] = bookmarked.orFalse()
    if (synced != null) map["synced"] = synced.orFalse()
    if (voiceSynced != null) map["voiceSynced"] = voiceSynced.orFalse()
    if (imageSynced != null) map["imageSynced"] = imageSynced.orFalse()
    if (email != null) map["email"] = email.orEmpty()
    return map
}

fun VerbForms.toMap(): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    map["pastSimple"] = pastSimple
    map["pastParticiple"] = pastParticiple
    return map
}