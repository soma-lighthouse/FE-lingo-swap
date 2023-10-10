package com.lighthouse.lingo_talk

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.constriant.ViewType.Companion.findClassByItsName
import com.lighthouse.domain.entity.response.server_driven.ContentVO
import com.lighthouse.domain.entity.response.server_driven.ViewTypeVO
import java.lang.reflect.Type

class ViewTypeDeserializer : JsonDeserializer<ViewTypeVO> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): ViewTypeVO {
        val jsonObject = json?.asJsonObject ?: throw IllegalArgumentException("Json Parsing 실패")
        val id = jsonObject["id"].asInt
        val viewTypeString = jsonObject["name"].asString
        val viewType: ViewType = findClassByItsName(viewTypeString)
        val content = jsonObject["contents"].asJsonObject
        val contentVO: Type = ViewType.findViewTypeClassByItsName(viewTypeString)
        val deserialize: ContentVO = Gson().fromJson(content, contentVO)
        return ViewTypeVO(id, viewType, deserialize)
    }
}