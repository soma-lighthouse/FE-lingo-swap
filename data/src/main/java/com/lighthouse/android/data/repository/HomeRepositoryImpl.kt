package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.mapping.toDTO
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.domain.entity.request.UploadFilterVO
import com.lighthouse.domain.entity.response.FilterVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.UserProfileVO
import com.lighthouse.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val datasource: HomeRemoteDataSource,
    private val local: LocalPreferenceDataSource,
) : HomeRepository {
    override fun getIfFilterUpdated(): Boolean = local.getIfFilterUpdated()

    override fun saveIfFilterUpdated(update: Boolean) = local.saveIfFilterUpdated(update)

    override fun getMatchedUser(
        next: Int?,
        pageSize: Int?,
    ): Flow<UserProfileVO> {
        val userId = local.getUUID() ?: ""
        return datasource.getMatchedUser(userId, next, pageSize).map {
            it.toVO()
        }
    }

    override fun getFilterSetting(): Flow<FilterVO> {
        val userId = local.getUUID() ?: ""
        return datasource.getFilterSetting(userId)
            .map {
                it.toVO()
            }
    }

    override fun uploadFilterSetting(filter: UploadFilterVO): Flow<Boolean> {
        val userId = local.getUUID() ?: ""
        return datasource.uploadFilterSetting(
            userId,
            filter.toDTO()
        )
    }

    override fun saveLanguageFilter(languages: List<LanguageVO>) {
        local.saveLanguageSetting(languages)
    }

    override fun getLanguageFilter(): List<LanguageVO> {
        return local.getLanguageSetting()
    }
}