package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.response.TestDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

fun interface DrivenRemoteDataSource {
    fun getDriven(): Flow<Resource<TestDTO>>
}