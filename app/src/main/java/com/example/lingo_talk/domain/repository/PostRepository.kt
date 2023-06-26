package com.example.lingo_talk.domain.repository

import com.example.lingo_talk.data.model.PostsItem

interface PostRepository {
    suspend fun getPosts(): List<PostsItem>?
    suspend fun savePosts(postsItem: List<PostsItem>): List<PostsItem>
}