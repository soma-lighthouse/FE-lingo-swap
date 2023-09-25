package com.lighthouse.android.data.repository

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.mapping.toDTO
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.domain.constriant.Resource
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

    override fun getFilterSetting(): Flow<Resource<FilterVO>> {
        val userId = local.getUUID() ?: ""
        return datasource.getFilterSetting(userId)
            .map {
                when (it) {
                    is Resource.Success -> Resource.Success(it.data!!.toVO())
                    else -> Resource.Error(it.message ?: "No message found")
                }
            }
    }

    override fun uploadFilterSetting(filter: UploadFilterVO): Flow<Resource<Boolean>> {
        val userId = local.getUUID() ?: ""
        return datasource.uploadFilterSetting(
            userId,
            filter.toDTO()
        )
            .map {
                when (it) {
                    is Resource.Success -> Resource.Success(it.data!!)
                    else -> Resource.Error(it.message ?: "No message found")
                }
            }
    }

    override fun saveLanguageFilter(languages: List<LanguageVO>) {
        local.saveLanguageSetting(languages)
    }

    override fun getLanguageFilter(): List<LanguageVO> {
        return local.getLanguageSetting()
    }
}