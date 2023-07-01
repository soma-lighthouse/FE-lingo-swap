package com.lighthouse.domain.repository

import com.lighthouse.domain.response.PostVO

interface PostRepository {
    suspend fun getPostFromAPI(): List<PostVO>
}