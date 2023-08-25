package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.response.UserProfileDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRemoteDataSource {
    fun getMatchedUser(
        userId: String,
        next: Int?,
        pageSize: Int?,
    ): Flow<Resource<UserProfileDTO>>
}