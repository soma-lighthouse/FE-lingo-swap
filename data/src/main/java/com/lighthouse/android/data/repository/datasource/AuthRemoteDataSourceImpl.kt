package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.api.AuthApiService
import com.lighthouse.android.data.model.response.InterestDTO
import com.lighthouse.android.data.repository.datasourceimpl.NetworkResponse
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthApiService,
) : AuthRemoteDataSource, NetworkResponse() {
    override fun getInterestList(): Flow<Resource<List<InterestDTO>>> = flow {
        emit(changeResult(api.getInterestList()))
    }
}