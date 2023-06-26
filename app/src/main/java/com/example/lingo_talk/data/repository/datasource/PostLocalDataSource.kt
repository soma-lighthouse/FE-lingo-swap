package com.example.lingo_talk.data.repository.datasource

import com.example.lingo_talk.data.model.PostsItem

interface PostLocalDataSource {
    suspend fun getPostFromDB(): List<PostsItem>
    suspend fun savePostToDB(posts: List<PostsItem>)
    suspend fun clearAll()
}