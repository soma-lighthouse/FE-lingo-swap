package com.lighthouse.android.data.api

import com.lighthouse.android.data.model.PostsDTO
import com.lighthouse.domain.response.PostVO
import retrofit2.Response
import retrofit2.http.GET

interface PostAPIService {
    @GET("/posts")
    suspend fun getPosts(): Response<List<PostsDTO>>
}