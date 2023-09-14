package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.DrivenRepository
import javax.inject.Inject

class TestUseCase @Inject constructor(
    private val drivenRepository: DrivenRepository,
) {
    fun invoke() = drivenRepository.getDriven()
}