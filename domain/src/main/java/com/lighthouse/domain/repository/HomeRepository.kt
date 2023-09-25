package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.UploadFilterVO
import com.lighthouse.domain.entity.response.FilterVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.UserProfileVO
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getMatchedUser(
        next: Int?,
        pageSize: Int?,
    ): Flow<Resource<UserProfileVO>>

    fun saveLanguageFilter(languages: List<LanguageVO>)
    fun getLanguageFilter(): List<LanguageVO>

    fun getFilterSetting(): Flow<Resource<FilterVO>>
    fun uploadFilterSetting(filter: UploadFilterVO): Flow<Resource<Boolean>>
}