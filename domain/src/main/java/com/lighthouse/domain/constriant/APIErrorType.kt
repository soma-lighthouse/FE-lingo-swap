package com.lighthouse.domain.constriant

enum class APIErrorType(
    val code: Int?,
    val uiMessage: String,
    val errorType: ErrorTypeHandling,
) {

    FIRST_LOGIN(
        40400,
        "First login. Please try again later.",
        ErrorTypeHandling.DIRECT_AND_DIALOG
    ),

    INVALID_ACCESS(
        403,
        "Invalid access. Please try again later.",
        ErrorTypeHandling.DIRECT_AND_DIALOG
    ),

    UNKNOWN(
        null,
        "Server error. Please try again later.",
        ErrorTypeHandling.TOAST
    );
}