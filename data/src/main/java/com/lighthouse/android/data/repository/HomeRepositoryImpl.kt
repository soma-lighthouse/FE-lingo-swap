package com.lighthouse.android.data.repository

import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.response.vo.UserProfileVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val datasource: HomeRemoteDataSource,
) : HomeRepository {
    override fun getMatchedUser(
        userId: Int,
        next: Int?,
        pageSize: Int?,
    ): Flow<Resource<UserProfileVO>> {
        return datasource.getMatchedUser(userId, next, pageSize).map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!.toVO())
                else -> Resource.Error(it.message ?: "No message found")
            }
        }
    }
}