package com.lighthouse.android.data.repository.datasource

import com.lighthouse.android.data.model.request.RegisterInfoDTO
import com.lighthouse.android.data.model.response.CountryForm
import com.lighthouse.android.data.model.response.InterestForm
import com.lighthouse.android.data.model.response.LanguageForm
import com.lighthouse.android.data.model.response.PreSignedUrlDTO
import com.lighthouse.android.data.model.response.UserTokenDTO
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface AuthRemoteDataSource {
    fun getInterestList(idToken: String): Flow<InterestForm>

    fun getLanguageList(idToken: String): Flow<LanguageForm>

    fun getCountryList(idToken: String): Flow<CountryForm>

    fun registerUser(idToken: String?, info: RegisterInfoDTO): Flow<UserTokenDTO>

    fun getPreSigned(fileName: String): Flow<PreSignedUrlDTO>

    fun uploadImg(url: String, profilePath: RequestBody): Flow<Boolean>

    fun postGoogleLogin(idToken: String?): Flow<UserTokenDTO>

    fun updateImgInfo(url: String, uuid: String): Flow<Boolean>
}