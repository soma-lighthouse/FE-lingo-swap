package com.lighthouse.android.data.repository.datasourceimpl

import android.util.Log
import com.google.gson.Gson
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.model.response.ErrorDTO
import com.lighthouse.domain.constriant.ErrorTypeHandling
import com.lighthouse.domain.entity.response.vo.LighthouseException
import retrofit2.Response

abstract class NetworkResponse {
    protected fun <T, R : BaseResponse<T>> changeResult(response: Response<R>): T {
        if (response.isSuccessful) {
            return response.body()!!.data
        } else {
            throw errorHandler(response)
        }
    }

    protected fun <R> errorHandler(response: Response<R>): LighthouseException {
        val errorBody = response.errorBody()?.string()

        return try {
            val errorResponse: BaseResponse<*> =
                Gson().fromJson(errorBody, BaseResponse::class.java)
            val errorMsg = errorResponse.data?.toString() ?: "{}"

            val body = errorMsg.getErrorMsg()
            val code = errorResponse.code
            val message = body.message ?: errorResponse.message

            val errorType = ErrorTypeHandling.fromString(body.type ?: "NONE")
            Log.d("ERROR_HANDLING_msg", code.toString())

            LighthouseException(code, message, errorType).addErrorMsg()
        } catch (e: Exception) {
            LighthouseException(null, null).addErrorMsg()
        }


    }

    private fun String.getErrorMsg(): ErrorDTO {
        Log.d("ERROR", this)

        if (this.isEmpty()) {
            // Handle the case of an empty error string
            return ErrorDTO(null, null)
        }

        val keyValuePairs = this
            .substring(1, this.length - 1) // Remove curly braces
            .split(", ")
            .map { it.split("=") }
            .filter { it.size == 2 } // Filter out pairs that don't have exactly one "="
            .associate { it[0] to it[1] }

        return ErrorDTO(keyValuePairs["message"] ?: "", keyValuePairs["type"] ?: "")
    }

}
