package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.UserProfileVO
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getMatchedUser(
        userId: String,
        next: Int?,
        pageSize: Int?,
    ): Flow<Resource<UserProfileVO>>
}