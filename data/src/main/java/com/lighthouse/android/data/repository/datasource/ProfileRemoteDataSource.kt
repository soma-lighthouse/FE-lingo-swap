package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.request.UpdateProfileDTO
import com.lighthouse.android.data.model.request.UploadFilterDTO
import com.lighthouse.android.data.model.response.MyQuestionResponse
import com.lighthouse.android.data.model.response.ProfileDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRemoteDataSource {
    fun getProfileDetail(userId: String): Flow<Resource<ProfileDTO>>
    fun getMyQuestions(userId: String): Flow<Resource<MyQuestionResponse>>

    fun updateProfile(uuid: String, newProfile: UpdateProfileDTO): Flow<Resource<Boolean>>
    fun updateFilter(uuid: String, newFilter: UploadFilterDTO): Flow<Resource<Boolean>>
}