package com.lighthouse.domain.constriant

enum class ErrorTypeHandling {
    TOAST,
    DIALOG,
    DIRECT_AND_DIALOG,
    NONE;

    companion object {
        fun fromString(value: String): ErrorTypeHandling {
            return when (value) {
                "TOAST" -> TOAST
                "DIALOG" -> DIALOG
                "DIRECT_AND_DIALOG" -> DIRECT_AND_DIALOG
                else -> NONE
            }
        }
    }
}