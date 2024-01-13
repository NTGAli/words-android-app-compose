package com.ntg.mywords.util
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.ntg.mywords.model.response.SenseItem
import java.lang.reflect.Type

class SseqDeserializer : JsonDeserializer<List<List<List<Any>>>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: com.google.gson.JsonDeserializationContext?
    ): List<List<List<Any>>> {
        val sseq = mutableListOf<List<List<Any>>>()
        json?.asJsonArray?.forEach { outerArray ->
            val outerList = mutableListOf<List<Any>>()
            outerArray.asJsonArray.forEach { innerArray ->
                val innerList = mutableListOf<Any>()
                innerArray.asJsonArray.forEach { element ->
                    val deserializedElement = if (element.isJsonObject) {
                        context?.deserialize(element, SenseItem::class.java) ?: element
                    } else {
                        element.asString
                    }
                    innerList.add(deserializedElement)
                }
                outerList.add(innerList)
            }
            sseq.add(outerList)
        }
        return sseq
    }
}