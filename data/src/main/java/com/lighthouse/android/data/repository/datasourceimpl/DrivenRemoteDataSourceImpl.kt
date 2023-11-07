package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DrivenRemoteDataSourceImpl @Inject constructor(
    private val drivenServer: DrivenApiService,
) : DrivenRemoteDataSource, NetworkResponse() {
    override fun getDriven(): Flow<Boolean> = flow {
        val response = drivenServer.getDriven()
        if (response.isSuccessful) {
            emit(true)
        } else {
            throw errorHandler(response)
        }
    }

}