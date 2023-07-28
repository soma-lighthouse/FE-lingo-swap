package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.model.UserProfileDTO
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRemoteDataSourceImpl @Inject constructor(
    private val homeApiService: HomeApiService,
) : HomeRemoteDataSource, NetworkResponse() {
    override fun getMatchedUser(): Flow<Resource<UserProfileDTO>> = flow {
        emit(changeResult(homeApiService.getMatchedUser()))
    }.flowOn(Dispatchers.IO)
}