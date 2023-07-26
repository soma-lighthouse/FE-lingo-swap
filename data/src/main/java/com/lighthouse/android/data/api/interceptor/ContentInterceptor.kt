package com.lighthouse.android.data.api.interceptor

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

object ContentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        return try {
            val responseData = response.body?.string()
            val responseJson = convertStringToJsonObject(responseData)
            val contentsArray = getContentsList(responseJson)
            val contentsString = contentsArray.toString()
            response.newBuilder().body(contentsString.toResponseBody()).build()
        } catch (e: Exception) {
            response
        }
    }

    private fun convertStringToJsonObject(jsonString: String?): JsonObject {
        return JsonParser.parseString(jsonString).asJsonObject
    }

    private fun getContentsList(json: JsonObject): JsonArray {
        return json.getAsJsonObject("data").getAsJsonArray("screenContents")
    }
}