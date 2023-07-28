package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.model.BaseResponse
import com.lighthouse.android.data.util.HttpResponseStatus
import com.lighthouse.domain.constriant.Resource
import retrofit2.Response

abstract class NetworkResponse {
    protected fun <T, R : BaseResponse<T>> changeResult(response: Response<R>): Resource<T> {
        val body = response.body()
        body?.let {
            return when (HttpResponseStatus.create(body.code)) {
                HttpResponseStatus.OK -> {
                    body.let {
                        Resource.Success(body.data)
                    }
                    Resource.Error("data not found")
                }

                else -> {
                    Resource.Error(body.message)
                }
            }
        }
        return Resource.Error("No response Found")
    }
}