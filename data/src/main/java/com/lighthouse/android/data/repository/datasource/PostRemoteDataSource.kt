package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.PostsDTO
import com.lighthouse.domain.response.PostVO
import retrofit2.Response

interface PostRemoteDataSource {
    suspend fun getPosts(): Response<List<PostsDTO>>
}