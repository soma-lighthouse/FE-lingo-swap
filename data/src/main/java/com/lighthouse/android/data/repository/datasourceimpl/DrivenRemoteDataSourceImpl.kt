package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.response.server_driven.ViewTypeVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DrivenRemoteDataSourceImpl @Inject constructor(
    private val drivenServer: DrivenApiService,
) : DrivenRemoteDataSource, NetworkResponse() {
    override fun getDriven(): Flow<Resource<List<ViewTypeVO>>> = flow {
//        emit(changeResult(drivenServer.getDriven())
    }

}