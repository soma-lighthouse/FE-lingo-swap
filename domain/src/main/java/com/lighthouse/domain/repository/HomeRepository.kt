package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.response.vo.UserProfileVO
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getMatchedUser(
        userId: Int,
        next: Int?,
        pageSize: Int?,
    ): Flow<Resource<UserProfileVO>>
}