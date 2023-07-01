package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.PostRepository
import com.lighthouse.domain.response.PostVO

class GetPostUseCase(private val postRepository: PostRepository) {
    suspend fun execute() = postRepository.getPostFromAPI()
}
