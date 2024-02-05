package com.ntg.vocabs.model.response

data class DefItem(
    val vd: String,
    val sseq: List<List<List<Any>>>?,
)

data class SenseItem(
//    val sn: String,
    val dt: List<List<DefinitionText>> ? = null,
//    val vis: List<Visualization>,
//    val sls: List<String>? = null
)

data class DefinitionText(
    val text: String? = null,
//    val bc: String? = null,
//    val sx: String? = null,
//    val wi: String? = null
)

data class Visualization(
    val t: String,
    val wi: String? = null
)