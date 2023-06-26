package com.example.lingo_talk.data.api

import com.example.lingo_talk.data.model.Posts
import retrofit2.Response
import retrofit2.http.GET

interface PostAPIService {
    @GET("/posts")
    suspend fun getPosts(): Response<Posts>
}