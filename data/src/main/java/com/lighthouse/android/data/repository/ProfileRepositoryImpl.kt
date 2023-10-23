package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.mapping.toUpdateFilterDTO
import com.lighthouse.android.data.model.mapping.toUpdateProfileDTO
import com.lighthouse.android.data.repository.datasource.ProfileRemoteDataSource
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
    override fun getProfileDetail(uuid: String): Flow<ProfileVO> =
        datasource.getProfileDetail(uuid)
            .map {
                it.toVO()
            }

    override fun getMyQuestions(): Flow<List<MyQuestionsVO>> =
        datasource.getMyQuestions(local.getUUID() ?: "")
            .map {
                it.myQuestionList.map { questions ->
                    questions.toVO()
                }
            }

    override fun updateProfile(newProfile: RegisterInfoVO): Flow<Boolean> {
        return datasource.updateProfile(local.getUUID() ?: "", newProfile.toUpdateProfileDTO())
    }

    override fun updateFilter(newFilter: RegisterInfoVO): Flow<Boolean> {
        return datasource.updateFilter(local.getUUID() ?: "", newFilter.toUpdateFilterDTO())
            .map {
                local.saveIfFilterUpdated(true)
                true
            }
    }

    override fun getUUID() = local.getUUID() ?: ""

    override fun setPushEnabled(enabled: Boolean) {
        local.setPushEnabled(enabled)
    }

    override fun getPushEnabled(): Boolean {
        return local.getPushEnabled()
    }
}