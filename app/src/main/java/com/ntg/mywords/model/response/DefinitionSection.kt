package com.ntg.mywords.model.response

import com.google.gson.annotations.SerializedName

data class DefinitionSection(
    @SerializedName("sseq")
    val senseSequence: List<List<List<Any?>?>?>?,
    val vd: String?
)