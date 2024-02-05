package com.ntg.vocabs.model.response

import com.google.gson.annotations.SerializedName

data class WordDataItem(
    val date: String?,
    @SerializedName("def")
    val definitionSection: List<DefItem>?,
    @SerializedName("dros")
    val definedRunOns: List<DefinedRunOns>?,
//    @SerializedName("et")
//    val etymology: List<List<String>>?,
    @SerializedName("fl")
    val functionalLabel: String?,
    @SerializedName("hom")
    val homograph: Int?,
    @SerializedName("hwi")
    val headwordInformation: HeadwordInformation?,
    @SerializedName("ins")
    val inflections: List<Inflection>?,
    @SerializedName("meta")
    val entryMetadata: EntryMetadata?,
    @SerializedName("shortdef")
    val shortDefinitions: List<String>?,
    @SerializedName("uros")
    val undefinedRunOns: List<UndefinedRunOns>?
)