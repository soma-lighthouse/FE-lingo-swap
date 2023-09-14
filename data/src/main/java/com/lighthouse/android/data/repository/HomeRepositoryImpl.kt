package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.UserProfileVO
import com.lighthouse.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val datasource: HomeRemoteDataSource,
    private val local: LocalPreferenceDataSource,
) : HomeRepository {
    override fun getMatchedUser(
        next: Int?,
        pageSize: Int?,
    ): Flow<Resource<UserProfileVO>> {
        val userId = local.getUUID() ?: ""
        return datasource.getMatchedUser(userId, next, pageSize).map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!.toVO())
                else -> Resource.Error(it.message ?: "No message found")
            }
        }
    }
}