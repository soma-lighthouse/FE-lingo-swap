package com.lighthouse.domain.usecase

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO
import com.lighthouse.domain.entity.response.vo.MyQuestionsVO
import com.lighthouse.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMyQuestionsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    fun invoke(): Flow<Resource<List<BoardQuestionVO>>> {
        return profileRepository.getMyQuestions()
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        val flatMappedData = flatMapToList(result.data!!)
                        Resource.Success(flatMappedData)
                    }

                    else -> Resource.Error(result.message ?: "No Message found")
                }
            }
    }


    private fun flatMapToList(data: List<MyQuestionsVO>): List<BoardQuestionVO> {
        val questions = mutableListOf<BoardQuestionVO>()
        data.forEach {
            questions.addAll(flatMapToSingle(it, it.categoryId))
        }

        return questions

    }

    private fun flatMapToSingle(data: MyQuestionsVO, categoryId: Int): List<BoardQuestionVO> {
        data.questions.map {
            it.categoryId = categoryId
        }

        return data.questions
    }
}