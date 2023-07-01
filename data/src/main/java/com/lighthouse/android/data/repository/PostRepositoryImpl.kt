package com.lighthouse.android.data.repository

import android.util.Log
import com.lighthouse.android.data.model.PostsDTO
import com.lighthouse.android.data.repository.datasource.PostRemoteDataSource
import com.lighthouse.domain.repository.PostRepository
import com.lighthouse.domain.response.PostVO
import java.lang.Exception

class PostRepositoryImpl(
    private val postRemoteDataSource: PostRemoteDataSource,
) : PostRepository {
    override suspend fun getPostFromAPI(): List<PostVO> {
        lateinit var postList: List<PostsDTO>

        try {
            val response = postRemoteDataSource.getPosts()
            val body = response.body()
            if (body != null) {
                postList = body
            }
        } catch (e: Exception) {
            Log.i("APIERROR", e.message.toString())
        }
        return postList.map {
            it.toVO()
        }
    }

}