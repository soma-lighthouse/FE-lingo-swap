package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.model.request.UploadFilterDTO
import com.lighthouse.android.data.model.response.FilterDTO
import com.lighthouse.android.data.model.response.UserProfileDTO
import com.lighthouse.android.data.repository.datasource.HomeRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRemoteDataSourceImpl @Inject constructor(
    private val api: HomeApiService,
) : HomeRemoteDataSource, NetworkResponse() {
    override fun getMatchedUser(
        userId: String,
        next: Int?,
        pageSize: Int?,
    ): Flow<UserProfileDTO> = flow {
        emit(changeResult(api.getMatchedUser(userId, next, pageSize)))
    }

    override fun getFilterSetting(userId: String): Flow<FilterDTO> = flow {
        emit(changeResult(api.getFilterSetting(userId)))
    }

    override fun uploadFilterSetting(
        userId: String,
        filter: UploadFilterDTO
    ): Flow<Boolean> =
        flow {
            val response = api.uploadFilterSetting(userId, filter)
            if (response.isSuccessful) {
                emit(true)
            } else {
                throw errorHandler(response)
            }
        }
}