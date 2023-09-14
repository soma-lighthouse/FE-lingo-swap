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
        "회원가입을 먼저 진행해주세요",
        ErrorTypeHandling.DIRECT
    ),

    REFRESH_TOKEN_EXPIRED(
        40101,
        "",
        "로그인이 만료되었습니다 다시 로그인 해주세요",
        ErrorTypeHandling.DIRECT
    ),

    UNKNOWN(
        null,
        null,
        "서버가 응답하지 않습니다 잠시후 시도해주세요",
        ErrorTypeHandling.TOAST
    );
}