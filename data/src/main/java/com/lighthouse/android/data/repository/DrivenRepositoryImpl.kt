package com.lighthouse.android.data.repository

import com.lighthouse.android.data.repository.datasource.DrivenRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.TestVO
import com.lighthouse.domain.repository.DrivenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DrivenRepositoryImpl @Inject constructor(
    private val drivenRemoteDataSource: DrivenRemoteDataSource,
) : DrivenRepository {
    //    override fun getDriven(): Flow<List<ViewTypeVO>> =
//        drivenRemoteDataSource.getDriven()
    override fun getDriven(): Flow<Resource<TestVO>> =
        drivenRemoteDataSource.getDriven()
            .map {
                when (it) {
                    is Resource.Success -> Resource.Success(it.data!!.toVO())
                    else -> Resource.Error(it.message ?: "Server Error")
                }
            }
}