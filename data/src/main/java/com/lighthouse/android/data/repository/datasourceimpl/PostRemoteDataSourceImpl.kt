package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.PostAPIService
import com.lighthouse.android.data.model.PostsDTO
import com.lighthouse.android.data.repository.datasource.PostRemoteDataSource
import com.lighthouse.domain.response.PostVO
import retrofit2.Response

class PostRemoteDataSourceImpl(
    private val api: PostAPIService
) : PostRemoteDataSource {
    override suspend fun getPosts(): Response<List<PostsDTO>> {
        return api.getPosts()
    }
}