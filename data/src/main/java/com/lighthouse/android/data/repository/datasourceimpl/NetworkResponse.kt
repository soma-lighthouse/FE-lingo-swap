package com.lighthouse.android.data.repository.datasourceimpl

import com.google.gson.Gson
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.TestDTO
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.LighthouseException
import retrofit2.Response

abstract class NetworkResponse {
    protected fun <T, R : BaseResponse<T>> changeResult(response: Response<R>): Resource<T> {
        if (response.isSuccessful) {
            return Resource.Success(response.body()!!.data)
        } else {
            val errorBody = response.errorBody()?.string()
            val errorResponse: BaseResponse<*> =
                Gson().fromJson(errorBody, BaseResponse::class.java)

            val errorMsg = errorResponse.data.toString()

            throw if (errorMsg == "{}") {
                LighthouseException(
                    code = errorResponse.code,
                    message = errorResponse.message
                ).addErrorMsg()
            } else {
                LighthouseException(
                    code = errorResponse.code,
                    message = errorMsg.getErrorMsg()
                )
            }
        }
    }

    private fun String.getErrorMsg(): String {
        val serverMsg = Gson().fromJson(this, TestDTO::class.java)
        return serverMsg.msg!!
    }
}
