package com.example.lingo_talk.domain.usecase

import com.example.lingo_talk.data.model.PostsItem
import com.example.lingo_talk.domain.repository.PostRepository

class SavePostUseCase(private val postRepository: PostRepository) {
    suspend fun execute(posts: List<PostsItem>) = postRepository.savePosts(posts)
}