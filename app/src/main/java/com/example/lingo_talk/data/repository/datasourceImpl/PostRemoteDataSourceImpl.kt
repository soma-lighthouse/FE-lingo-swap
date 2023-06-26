package com.example.lingo_talk.data.repository.datasourceImpl

import com.example.lingo_talk.data.api.PostAPIService
import com.example.lingo_talk.data.model.Posts
import com.example.lingo_talk.data.repository.datasource.PostRemoteDataSource
import retrofit2.Response

class PostRemoteDataSourceImpl(
    private val api: PostAPIService
) : PostRemoteDataSource {
    override suspend fun getPosts(): Response<Posts> {
        return api.getPosts()
    }
}