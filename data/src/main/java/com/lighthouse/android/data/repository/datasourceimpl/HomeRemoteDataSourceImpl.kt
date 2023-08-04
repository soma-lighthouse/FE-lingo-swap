package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.model.UserProfileDTO
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRemoteDataSourceImpl @Inject constructor(
    private val api: HomeApiService,
) : HomeRemoteDataSource, NetworkResponse() {
    override fun getMatchedUser(
        userId: Int,
        next: Int?,
        pageSize: Int?,
    ): Flow<Resource<UserProfileDTO>> = flow {
        emit(changeResult(api.getMatchedUser(userId, next, pageSize)))
    }

}