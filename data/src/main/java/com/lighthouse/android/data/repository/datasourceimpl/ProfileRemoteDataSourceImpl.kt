package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.ProfileApiService
import com.lighthouse.android.data.model.response.ProfileDTO
import com.lighthouse.android.data.repository.datasource.ProfileRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRemoteDataSourceImpl @Inject constructor(
    private val api: ProfileApiService,
) : ProfileRemoteDataSource, NetworkResponse() {
    override fun getProfileDetail(userId: Int): Flow<Resource<ProfileDTO>> = flow {
        emit(changeResult(api.getProfileDetail(userId)))
    }
}