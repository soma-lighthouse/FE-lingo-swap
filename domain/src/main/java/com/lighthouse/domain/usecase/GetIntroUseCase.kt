package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.IntroRepository


class GetIntroUseCase(private val introRepository: IntroRepository ) {
    suspend fun execute() = introRepository.getIntro()

}
