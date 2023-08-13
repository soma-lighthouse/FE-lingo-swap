package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.response.InterestDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {
    fun getInterestList(): Flow<Resource<List<InterestDTO>>>
}