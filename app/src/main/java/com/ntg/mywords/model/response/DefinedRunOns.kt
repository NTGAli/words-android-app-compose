package com.ntg.mywords.model.response

import com.google.gson.annotations.SerializedName

data class DefinedRunOns(
//    @SerializedName("def")
//    val definitionSections: List<DefinitionSection>?,
    val drp: String?,
    @SerializedName("vrs")
    val variants: List<Variants>?
)