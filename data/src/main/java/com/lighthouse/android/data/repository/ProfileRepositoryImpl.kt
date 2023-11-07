package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.mapping.toUpdateFilterDTO
import com.lighthouse.android.data.model.mapping.toUpdateProfileDTO
import com.lighthouse.android.data.repository.datasource.ProfileRemoteDataSource
import com.lighthouse.android.data.util.LocalKey
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
        datasource.getMyQuestions(local.getString(LocalKey.USER_ID) ?: "")
            .map {
                it.myQuestionList.map { questions ->
                    questions.toVO()
                }
            }

    override fun updateProfile(newProfile: RegisterInfoVO): Flow<Boolean> {
        return datasource.updateProfile(
            local.getString(LocalKey.USER_ID) ?: "",
            newProfile.toUpdateProfileDTO()
        )
    }

    override fun updateFilter(newFilter: RegisterInfoVO): Flow<Boolean> {
        return datasource.updateFilter(
            local.getString(LocalKey.USER_ID) ?: "",
            newFilter.toUpdateFilterDTO()
        )
            .map {
                local.save(LocalKey.FILTER_UPDATED, true)
                true
            }
    }

    override fun getUUID() = local.getString(LocalKey.USER_ID) ?: ""

    override fun setPushEnabled(enabled: Boolean) {
        local.save(LocalKey.PUSH_ENABLED, enabled)
    }

    override fun getPushEnabled(): Boolean {
        return local.getBoolean(LocalKey.PUSH_ENABLED)
    }
}