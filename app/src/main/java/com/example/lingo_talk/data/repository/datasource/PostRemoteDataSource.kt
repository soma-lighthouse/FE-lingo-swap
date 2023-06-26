package com.example.lingo_talk.data.repository.datasource

import com.example.lingo_talk.data.model.Posts
import retrofit2.Response

interface PostRemoteDataSource {
    suspend fun getPosts(): Response<Posts>
}