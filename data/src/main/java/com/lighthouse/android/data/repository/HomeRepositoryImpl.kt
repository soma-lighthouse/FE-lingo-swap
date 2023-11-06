package com.lighthouse.android.data.repository

import com.google.gson.reflect.TypeToken
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.mapping.toDTO
import com.lighthouse.android.data.remote.RemoteConfigDataSource
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import com.lighthouse.android.data.util.LocalKey
import com.lighthouse.domain.entity.request.UploadFilterVO
import com.lighthouse.domain.entity.request.UploadInterestVO
import com.lighthouse.domain.entity.response.FilterVO
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.UserProfileVO
import com.lighthouse.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val datasource: HomeRemoteDataSource,
    private val local: LocalPreferenceDataSource,
    private val remoteConfigDataSource: RemoteConfigDataSource
) : HomeRepository {
    override fun getIfFilterUpdated(): Boolean = local.getBoolean(LocalKey.FILTER_UPDATED)

    override fun saveIfFilterUpdated(update: Boolean) = local.save(LocalKey.FILTER_UPDATED, update)

    override fun getMatchedUser(
        next: Int?,
        pageSize: Int?,
    ): Flow<UserProfileVO> {
        val userId = local.getString(LocalKey.USER_ID)
        return datasource.getMatchedUser(userId, next, pageSize).map {
            it.toVO()
        }
    }

    override fun getFilterSetting(): Flow<FilterVO> {
        val userId = local.getString(LocalKey.USER_ID)
        return datasource.getFilterSetting(userId)
            .map {
                it.toVO()
            }
    }

    override fun uploadFilterSetting(): Flow<Boolean> {
        val userId = local.getString(LocalKey.USER_ID)
        val filter = UploadFilterVO(
            getCountryVO().map { it.code },
            getLanguageVO().map { mapOf("code" to it.code, "level" to it.level) },
            getInterestVO().map {
                UploadInterestVO(
                    it.category.code,
                    it.interests.map { i -> i.code })
            },
        )
        return datasource.uploadFilterSetting(
            userId,
            filter.toDTO()
        )
    }

    override fun saveLanguageVO(languages: List<LanguageVO>) {
        local.save(LocalKey.LANGUAGE_SETTING, languages)
    }

    override fun getLanguageVO(): List<LanguageVO> {
        val typeToken = object : TypeToken<List<LanguageVO>>() {}
        return local.getList(LocalKey.LANGUAGE_SETTING, typeToken)
    }

    override fun saveInterestVO(interests: List<InterestVO>) {
        local.save(LocalKey.INTEREST_SETTING, interests)
    }

    override fun getInterestVO(): List<InterestVO> {
        val typeToken = object : TypeToken<List<InterestVO>>() {}
        return local.getList(LocalKey.INTEREST_SETTING, typeToken)
    }

    override fun saveCountryVO(countries: List<CountryVO>) {
        local.save(LocalKey.COUNTRY_SETTING, countries)
    }

    override fun getCountryVO(): List<CountryVO> {
        val typeToken = object : TypeToken<List<CountryVO>>() {}
        return local.getList(LocalKey.COUNTRY_SETTING, typeToken)
    }

    override fun getRegion(): CountryVO {
        val typeToken = object : TypeToken<List<CountryVO>>() {}
        val result = local.getList(LocalKey.REGION_SETTING, typeToken)
        return if (result.isNotEmpty()) {
            result.first()
        } else {
            CountryVO("", "")
        }
    }

    override fun saveRegion(region: CountryVO) {
        local.save(LocalKey.REGION_SETTING, listOf(region))
    }

    override suspend fun fetchRemoteConfig(key: String): String {
        return remoteConfigDataSource.fetchRemoteConfig(key)
    }

    override fun clearAllData() {
        local.clearAllData()
    }
}