package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.AuthApiService
import com.lighthouse.android.data.model.request.RegisterInfoDTO
import com.lighthouse.android.data.model.response.CountryForm
import com.lighthouse.android.data.model.response.InterestForm
import com.lighthouse.android.data.model.response.LanguageForm
import com.lighthouse.android.data.model.response.PreSignedUrlDTO
import com.lighthouse.android.data.model.response.UserTokenDTO
import com.lighthouse.android.data.repository.datasource.AuthRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthApiService,
) : AuthRemoteDataSource, NetworkResponse() {
    override fun getInterestList(idToken: String): Flow<InterestForm> = flow {
        emit(changeResult(api.getInterestList(idToken)))
    }

    override fun getLanguageList(idToken: String): Flow<LanguageForm> = flow {
        emit(changeResult(api.getLanguageList(idToken)))
    }

    override fun getCountryList(idToken: String): Flow<CountryForm> = flow {
        emit(changeResult(api.getCountryList(idToken)))
    }

    override fun registerUser(
        idToken: String?,
        info: RegisterInfoDTO
    ): Flow<UserTokenDTO> =
        flow {
            emit(changeResult(api.registerUser(idToken ?: "", info)))
        }


    override fun getPreSigned(fileName: String): Flow<PreSignedUrlDTO> = flow {
        emit(changeResult(api.getPreSignedURL(mapOf("key" to fileName))))
    }

    override fun uploadImg(url: String, profilePath: RequestBody): Flow<Boolean> =
        flow {
            val response = api.uploadImg(url, profilePath)
            if (response.isSuccessful) {
                emit(true)
            } else {
                throw errorHandler(response)
            }
        }

    override fun postGoogleLogin(idToken: String?): Flow<UserTokenDTO> =
        flow {
            emit(changeResult(api.postGoogleLogin(idToken ?: "")))
        }

    override fun updateImgInfo(url: String, uuid: String): Flow<Boolean> = flow {
        val response = api.updateImgInfo(uuid, mapOf("profileImageUri" to url))
        if (response.isSuccessful) {
            emit(true)
        } else {
            throw errorHandler(response)
        }
    }
}