package com.lighthouse.android.data.repository

import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.response.dto.UserProfileVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeRepositoryImpl(
    private val homeRemoteDataSource: HomeRemoteDataSource,
) : HomeRepository {
    override fun getMatchedUser(): Flow<Resource<UserProfileVO>> {
        return homeRemoteDataSource.getMatchedUser().map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!.toVO())
                else -> Resource.Error(it.message ?: "No message found")
            }
        }
    }
}