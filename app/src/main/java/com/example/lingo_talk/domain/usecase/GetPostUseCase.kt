package com.example.lingo_talk.domain.usecase

import com.example.lingo_talk.domain.repository.PostRepository

class GetPostUseCase(private val postRepository: PostRepository) {
    suspend fun execute() = postRepository.getPosts()
}