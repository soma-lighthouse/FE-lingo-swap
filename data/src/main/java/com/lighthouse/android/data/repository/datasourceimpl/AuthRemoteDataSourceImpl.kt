package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.AuthApiService
import com.lighthouse.android.data.model.request.RegisterInfoDTO
import com.lighthouse.android.data.model.response.CountryForm
import com.lighthouse.android.data.model.response.InterestForm
import com.lighthouse.android.data.model.response.LanguageForm
import com.lighthouse.android.data.model.response.PreSignedURL
import com.lighthouse.android.data.model.response.UserTokenDTO
import com.lighthouse.android.data.repository.datasource.AuthRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthApiService,
) : AuthRemoteDataSource, NetworkResponse() {
    override fun getInterestList(): Flow<Resource<InterestForm>> = flow {
        emit(changeResult(api.getInterestList()))
    }

    override fun getLanguageList(): Flow<Resource<LanguageForm>> = flow {
        emit(changeResult(api.getLanguageList()))
    }

    override fun getCountryList(): Flow<Resource<CountryForm>> = flow {
        emit(changeResult(api.getCountryList()))
    }

    override fun registerUser(
        idToken: String?,
        info: RegisterInfoDTO
    ): Flow<Resource<UserTokenDTO>> =
        flow {
            emit(changeResult(api.registerUser(idToken ?: "", info)))
        }


    override fun getPreSigned(fileName: String): Flow<Resource<PreSignedURL>> = flow {
        emit(changeResult(api.getPreSignedURL(mapOf("key" to fileName))))
    }

    override fun uploadImg(url: String, profilePath: RequestBody): Flow<Resource<Boolean>> =
        flow {
            val response = api.uploadImg(url, profilePath)
            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                throw errorHandler(response)
            }
        }

    override fun postGoogleLogin(idToken: String?): Flow<Resource<UserTokenDTO>> =
        flow {
            emit(changeResult(api.postGoogleLogin(idToken ?: "")))
        }
}