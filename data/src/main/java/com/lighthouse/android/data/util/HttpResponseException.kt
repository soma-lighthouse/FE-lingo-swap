package com.lighthouse.android.data.util

class HttpResponseException(
    val status: HttpResponseStatus,
    val httpCode: Int,
    val errorUrl: String,
    msg: String,
    cause: Throwable?
) : Exception(msg, cause) {
}