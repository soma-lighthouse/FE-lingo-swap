package com.lighthouse.domain.repository

import com.lighthouse.domain.entity.response.FilterVO
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.UserProfileVO
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getMatchedUser(
        next: Int?,
        pageSize: Int?,
    ): Flow<UserProfileVO>

    fun saveLanguageVO(languages: List<LanguageVO>)
    fun getLanguageVO(): List<LanguageVO>
    fun saveInterestVO(interests: List<InterestVO>)
    fun getInterestVO(): List<InterestVO>
    fun saveCountryVO(countries: List<CountryVO>)
    fun getCountryVO(): List<CountryVO>

    fun getRegion(): CountryVO
    fun saveRegion(region: CountryVO)

    fun getFilterSetting(): Flow<FilterVO>
    fun uploadFilterSetting(): Flow<Boolean>

    fun getIfFilterUpdated(): Boolean
    fun saveIfFilterUpdated(update: Boolean)

    fun clearAllData()

    suspend fun fetchRemoteConfig(key: String): String
}