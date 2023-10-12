package com.lighthouse.domain.entity.response.vo

import com.lighthouse.domain.constriant.APIErrorType
import com.lighthouse.domain.constriant.ErrorTypeHandling

class LighthouseException(
    val code: Int?,
    override var message: String?,
    var errorType: ErrorTypeHandling = ErrorTypeHandling.NONE,
) : Exception() {

    fun addErrorMsg(): LighthouseException {
        if (code == null || code in FIXED_CODE) {
            val errorType = findAPIErrorType()
            if (this.message.isNullOrEmpty()) {
                this.message = errorType.uiMessage
            }
            this.errorType = errorType.errorType
        }
        return this
    }

    private fun findAPIErrorType(): APIErrorType {
        return APIErrorType.values().find { it.code == code }
            ?: APIErrorType.UNKNOWN
    }

    companion object {
        private val FIXED_CODE = listOf(403)
    }

}