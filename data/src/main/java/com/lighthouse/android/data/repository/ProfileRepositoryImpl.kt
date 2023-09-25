package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.mapping.toUpdateFilterDTO
import com.lighthouse.android.data.model.mapping.toUpdateProfileDTO
import com.lighthouse.android.data.repository.datasource.ProfileRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.MyQuestionsVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val datasource: ProfileRemoteDataSource,
    private val local: LocalPreferenceDataSource,
) : ProfileRepository {
    override fun getProfileDetail(uuid: String): Flow<Resource<ProfileVO>> =
        datasource.getProfileDetail(uuid)
            .map {
                when (it) {
                    is Resource.Success -> Resource.Success(it.data!!.toVO())
                    else -> Resource.Error(it.message ?: "No Message found")
                }
            }

    override fun getMyQuestions(): Flow<Resource<List<MyQuestionsVO>>> =
        datasource.getMyQuestions(local.getUUID() ?: "")
            .map {
                when (it) {
                    is Resource.Success -> Resource.Success(it.data!!.myQuestionList.map { questions ->
                        questions.toVO()
                    })

                    else -> Resource.Error(it.message ?: "No Message found")
                }
            }

    override fun updateProfile(newProfile: RegisterInfoVO): Flow<Boolean> {
        return datasource.updateProfile(local.getUUID() ?: "", newProfile.toUpdateProfileDTO())
            .map {
                when (it) {
                    is Resource.Success -> true
                    else -> false
                }
            }
    }

    override fun updateFilter(newFilter: RegisterInfoVO): Flow<Boolean> {
        return datasource.updateFilter(local.getUUID() ?: "", newFilter.toUpdateFilterDTO())
            .map {
                when (it) {
                    is Resource.Success -> {
                        local.saveIfFilterUpdated(true)
                        true
                    }

                    else -> false
                }
            }
    }

    override fun getUUID() = local.getUUID()
}