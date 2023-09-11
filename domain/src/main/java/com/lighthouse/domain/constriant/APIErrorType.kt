package com.lighthouse.domain.constriant

enum class APIErrorType(
    val code: Int?,
    val errorMsg: String?,
    val uiMessage: String,
    val errorType: ErrorTypeHandling,
) {
    FIRST_LOGIN(
        40400,
        "",
        "화원가입을 먼저 진행해주세요",
        ErrorTypeHandling.DIRECT
    ),

    UNKNOWN(
        null,
        null,
        "서버가 응답하지 않습니다 잠시후 시도해주세요",
        ErrorTypeHandling.TOAST
    );
}