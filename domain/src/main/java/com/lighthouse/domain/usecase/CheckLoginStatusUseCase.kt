package com.lighthouse.domain.usecase

import com.lighthouse.domain.constriant.LoginState
import com.lighthouse.domain.repository.AuthRepository
import java.time.Instant
import javax.inject.Inject

class CheckLoginStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend fun invoke(): LoginState {
        val expireTime = authRepository.getExpireTime()
        val refreshExpireTime = authRepository.getRefreshExpireTime()
        val currentTimeSecond = Instant.now().epochSecond

        return if (currentTimeSecond < expireTime) {
            LoginState.LOGIN_SUCCESS
        } else if (currentTimeSecond < refreshExpireTime) {
            LoginState.LOGIN_SUCCESS
        } else {
            LoginState.LOGIN_FAILURE
        }
    }
}

