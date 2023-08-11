package com.lighthouse.android.data.repository

import com.lighthouse.android.data.repository.datasource.ProfileRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.repository.ProfileRepository
import com.lighthouse.domain.response.vo.ProfileVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val datasource: ProfileRemoteDataSource,
) : ProfileRepository {
    override fun getProfileDetail(userId: Int): Flow<Resource<ProfileVO>> =
        datasource.getProfileDetail(userId)
            .map {
                when (it) {
                    is Resource.Success -> Resource.Success(it.data!!.toVO())
                    else -> Resource.Error(it.message ?: "No Message found")
                }
            }
}