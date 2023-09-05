package com.lighthouse.domain.constriant

enum class APIErrorType(
    val code: Int,
    val errorMsg: String,
    val uiMessage: String,
    val errorType: ErrorTypeHandling,
) {
    INVALID_FIELD(
        40000,
        "api.typeError",
        "허용되지 않은 필드입니다",
        ErrorTypeHandling.DIRECT
    ),

    LOGIN_ERROR(
        401,
        "api.loginError",
        "로그인에 실패했습니다",
        ErrorTypeHandling.DIRECT
    ),

    INVALID_ACCESS(
        401,
        "api.accessError",
        "허용되지 않은 접근입니다",
        ErrorTypeHandling.DIRECT
    ),

    UNKNOWN(
        404,
        "api.serverError",
        "서버가 응답하지 않습니다 잠시후 시도해주세요",
        ErrorTypeHandling.TOAST
    );
}