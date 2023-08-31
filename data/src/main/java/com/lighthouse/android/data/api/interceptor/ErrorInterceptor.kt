package com.lighthouse.android.data.api.interceptor

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.lighthouse.android.data.model.response.FailureDTO
import com.lighthouse.android.data.util.HttpResponseStatus
import okhttp3.Interceptor
import okhttp3.Response

object ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val response = chain.proceed(builder.build())

        if (HttpResponseStatus.create(response.code) != HttpResponseStatus.OK) {
            val failureDTO = response.getDto<FailureDTO<String>>()
            when (failureDTO.code) {
                40000 -> throw Exception(failureDTO.message)
                404 -> throw Exception(failureDTO.message)
            }
        }

        return response
    }

    private inline fun <reified T> Response.getDto(): T {
        val responseObject = JsonParser.parseString(body?.string()).asJsonObject
        return Gson().fromJson(responseObject, T::class.java)
    }
}
