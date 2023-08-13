package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.repository.datasource.AuthRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.entity.response.vo.InterestVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val localPreferenceDataSource: LocalPreferenceDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
) : AuthRepository {
    override fun getUserId(): String {
        return localPreferenceDataSource.getUUID()
    }

    override fun saveUserId(uuid: String) {
        localPreferenceDataSource.saveUUID(uuid)
    }

    override fun getInterestList(): Flow<Resource<List<InterestVO>>> =
        authRemoteDataSource.getInterestList(
        ).map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!.map { interest ->
                    interest.toVO()
                })

                else -> Resource.Error(it.message ?: "No message Found")
            }
        }
}