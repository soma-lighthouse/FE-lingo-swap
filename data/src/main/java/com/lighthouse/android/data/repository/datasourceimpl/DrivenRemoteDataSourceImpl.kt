package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.android.data.repository.datasourceimpl.NetworkResponse
import com.lighthouse.domain.entity.response.server_driven.ViewTypeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DrivenRemoteDataSourceImpl @Inject constructor(
    private val drivenServer: DrivenApiService,
) : DrivenRemoteDataSource, NetworkResponse() {
    override fun getDriven(): Flow<List<ViewTypeVO>> = flow {
        emit(drivenServer.getDriven().body()!!)
    }

}