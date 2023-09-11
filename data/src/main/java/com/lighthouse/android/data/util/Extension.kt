package com.lighthouse.android.data.util

import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.Response

inline fun <reified T> Response.getDto(): T {
    val responseObject = JsonParser.parseString(body?.string()).asJsonObject
    return Gson().fromJson(responseObject, T::class.java)
}