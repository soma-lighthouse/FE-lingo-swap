package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.BoardRepository
import javax.inject.Inject

class GetQuestionUseCase @Inject constructor(
    private val repository: BoardRepository,
) {
    fun invoke(category: Int, order: String, page: Int) =
        repository.getBoardQuestions(category, order, page)
}