package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.response.ProfileDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRemoteDataSource {
    fun getProfileDetail(userId: Int): Flow<Resource<ProfileDTO>>
}