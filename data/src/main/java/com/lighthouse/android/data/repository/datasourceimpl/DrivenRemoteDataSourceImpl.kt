package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.model.response.TestDTO
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DrivenRemoteDataSourceImpl @Inject constructor(
    private val drivenServer: DrivenApiService,
) : DrivenRemoteDataSource, NetworkResponse() {
    override fun getDriven(): Flow<Resource<TestDTO>> = flow {
        emit(changeResult(drivenServer.getDriven()))
    }

}