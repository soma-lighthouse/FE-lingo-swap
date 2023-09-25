package com.lighthouse.domain.usecase

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.MyQuestionsVO
import com.lighthouse.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyQuestionsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    fun invoke(): Flow<Resource<List<MyQuestionsVO>>> = profileRepository.getMyQuestions()
}