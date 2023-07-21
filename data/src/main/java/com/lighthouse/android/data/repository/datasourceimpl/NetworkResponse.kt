package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.util.HttpResponseException
import com.lighthouse.android.data.util.HttpResponseStatus
import retrofit2.Response

abstract class NetworkResponse {
    protected fun <T> checkResponse(response: Response<T>): T {
        if(response.isSuccessful) {
            return response.body()!!
        } else {
            val errorBody = response.errorBody()?.string()
            throw HttpResponseException(
                HttpResponseStatus.create(response.code()),
                response.code(),
                response.raw().request.url.toString(),
                "Http request failed [${response.code()}] ${response.message()}, $errorBody",
                Throwable(errorBody)
            )
        }
    }
}