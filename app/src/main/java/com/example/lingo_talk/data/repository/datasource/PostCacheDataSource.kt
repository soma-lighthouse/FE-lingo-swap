package com.example.lingo_talk.data.repository.datasource

import com.example.lingo_talk.data.model.PostsItem

interface PostCacheDataSource {
    suspend fun getPostFromCache(): List<PostsItem>
    suspend fun savePostToCache(posts: List<PostsItem>)
}