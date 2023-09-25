package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.ProfileApiService
import com.lighthouse.android.data.model.request.UpdateProfileDTO
import com.lighthouse.android.data.model.request.UploadFilterDTO
import com.lighthouse.android.data.model.response.MyQuestionResponse
import com.lighthouse.android.data.model.response.ProfileDTO
import com.lighthouse.android.data.repository.datasource.ProfileRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRemoteDataSourceImpl @Inject constructor(
    private val api: ProfileApiService,
) : ProfileRemoteDataSource, NetworkResponse() {
    override fun getProfileDetail(userId: String): Flow<Resource<ProfileDTO>> = flow {
        emit(changeResult(api.getProfileDetail(userId)))
    }

    override fun getMyQuestions(userId: String): Flow<Resource<MyQuestionResponse>> = flow {
        emit(changeResult(api.getMyQuestions(userId)))
    }

    override fun updateProfile(
        uuid: String,
        newProfile: UpdateProfileDTO
    ): Flow<Resource<Boolean>> =
        flow {
            val response = api.updateProfile(uuid, newProfile)
            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                throw errorHandler(response)
            }
        }

    override fun updateFilter(uuid: String, newFilter: UploadFilterDTO): Flow<Resource<Boolean>> =
        flow {
            val response = api.updateFilter(uuid, newFilter)
            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                throw errorHandler(response)
            }
        }
}