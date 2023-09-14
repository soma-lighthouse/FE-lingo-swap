package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.request.RegisterInfoDTO
import com.lighthouse.android.data.model.response.CountryForm
import com.lighthouse.android.data.model.response.InterestForm
import com.lighthouse.android.data.model.response.LanguageForm
import com.lighthouse.android.data.model.response.PreSignedURL
import com.lighthouse.android.data.model.response.UserTokenDTO
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface AuthRemoteDataSource {
    fun getInterestList(): Flow<Resource<InterestForm>>

    fun getLanguageList(): Flow<Resource<LanguageForm>>

    fun getCountryList(): Flow<Resource<CountryForm>>

    fun registerUser(info: RegisterInfoDTO): Flow<Resource<UserTokenDTO>>

    fun getPreSigned(fileName: String): Flow<Resource<PreSignedURL>>

    fun uploadImg(url: String, profilePath: RequestBody): Flow<Resource<Boolean>>

    fun postGoogleLogin(): Flow<Resource<UserTokenDTO>>
}