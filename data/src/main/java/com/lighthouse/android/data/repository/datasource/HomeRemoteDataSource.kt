package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.request.UploadFilterDTO
import com.lighthouse.android.data.model.response.FilterDTO
import com.lighthouse.android.data.model.response.UserProfileDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRemoteDataSource {
    fun getMatchedUser(
        userId: String,
        next: Int?,
        pageSize: Int?,
    ): Flow<Resource<UserProfileDTO>>

    fun getFilterSetting(userId: String): Flow<Resource<FilterDTO>>
    fun uploadFilterSetting(userId: String, filter: UploadFilterDTO): Flow<Resource<Boolean>>
}