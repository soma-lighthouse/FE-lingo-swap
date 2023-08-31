package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.util.HttpResponseStatus
import com.lighthouse.domain.constriant.Resource
import retrofit2.Response

abstract class NetworkResponse {
    protected fun <T, R : T> changeResult(response: Response<R>): Resource<T> {
        val body = response.body()

        response.code()
        body?.let {
            return when (HttpResponseStatus.create(response.code())) {
                HttpResponseStatus.OK -> {
                    Resource.Success(body)
                }

                else -> {
                    Resource.Error(response.message())
                }
            }
        }
        return Resource.Error("No response Found")
    }
}