package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.UserProfileDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRemoteDataSource {
    fun getMatchedUser(page: Int): Flow<Resource<UserProfileDTO>>
}