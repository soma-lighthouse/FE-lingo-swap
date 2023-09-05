package com.lighthouse.domain.entity.response.vo

import com.lighthouse.domain.constriant.APIErrorType
import com.lighthouse.domain.constriant.ErrorTypeHandling

class LighthouseException(
    val code: Int,
    var errorType: ErrorTypeHandling = ErrorTypeHandling.TOAST,
    override var message: String,
) : Exception() {

    fun addErrorMsg(): LighthouseException {
        val errorType = findAPIErrorType()
        this.message = errorType.uiMessage
        this.errorType = errorType.errorType
        return this
    }

    private fun findAPIErrorType(): APIErrorType {
        return APIErrorType.values().find { it.code == code && it.errorMsg == message }
            ?: APIErrorType.UNKNOWN
    }

}