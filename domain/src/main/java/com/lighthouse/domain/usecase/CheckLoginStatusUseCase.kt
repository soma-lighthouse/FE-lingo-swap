package com.lighthouse.domain.usecase

import com.lighthouse.domain.constriant.LoginState
import com.lighthouse.domain.repository.AuthRepository
import java.time.Instant
import javax.inject.Inject

class CheckLoginStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    fun invoke(): LoginState {
        val refreshExpireTime = authRepository.getRefreshExpireTime()
        val accessToken = authRepository.getAccessToken()
        val currentTimeSecond = Instant.now().epochSecond

        if (accessToken.isEmpty()) {
            return LoginState.LOGIN_FAILURE
        }

        return if (currentTimeSecond < refreshExpireTime) {
            LoginState.LOGIN_SUCCESS
        } else {
            LoginState.LOGIN_FAILURE
        }
    }
}

