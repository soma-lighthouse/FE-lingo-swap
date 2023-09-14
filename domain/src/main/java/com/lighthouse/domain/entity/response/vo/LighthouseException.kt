package com.lighthouse.domain.entity.response.vo

import com.lighthouse.domain.constriant.APIErrorType
import com.lighthouse.domain.constriant.ErrorTypeHandling

class LighthouseException(
    val code: Int?,
    override var message: String?,
    var errorType: ErrorTypeHandling = ErrorTypeHandling.TOAST,
) : Exception() {

    fun addErrorMsg(): LighthouseException {
        val errorType = findAPIErrorType()
        this.message = errorType.uiMessage
        this.errorType = errorType.errorType
        return this
    }

    private fun findAPIErrorType(): APIErrorType {
        return APIErrorType.values().find { it.code == code }
            ?: APIErrorType.UNKNOWN
    }

}