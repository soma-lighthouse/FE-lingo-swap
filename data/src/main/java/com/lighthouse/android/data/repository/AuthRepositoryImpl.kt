package com.lighthouse.android.data.repository

import android.util.Log
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.request.RegisterInfoDTO
import com.lighthouse.android.data.model.response.InterestDTO
import com.lighthouse.android.data.repository.datasource.AuthRemoteDataSource
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.domain.entity.response.vo.UserTokenVO
import com.lighthouse.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val localPreferenceDataSource: LocalPreferenceDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
) : AuthRepository {
    override fun getUserId(): String? {
        return localPreferenceDataSource.getUID()
    }

    override fun getInterestList(): Flow<Resource<List<InterestVO>>> =
        authRemoteDataSource.getInterestList().map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!.interest.map { interest ->
                    interest.toVO()
                })

                else -> Resource.Error(it.message ?: "No message Found")
            }
        }

    override fun getLanguageList(): Flow<Resource<List<LanguageVO>>> =
        authRemoteDataSource.getLanguageList().map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!.language.map { language ->
                    language.toVO()
                })

                else -> Resource.Error(it.message ?: "No message Found")
            }
        }

    override fun getCountryList(): Flow<Resource<List<CountryVO>>> =
        authRemoteDataSource.getCountryList().map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data!!.country.map { country ->
                    country.toVO()
                })

                else -> Resource.Error(it.message ?: "No message Found")
            }
        }

    override fun registerUser(info: RegisterInfoVO): Flow<Resource<Boolean>> {
        val tmp = RegisterInfoDTO(
            uid = info.uid ?: "null",
            name = info.name ?: "null",
            birthday = info.birthday ?: "null",
            email = info.email ?: "null",
            gender = info.gender ?: "null",
            region = info.region ?: "null",
            preferredInterests = info.preferredInterests?.map {
                InterestDTO(it.category, it.interest)
            } ?: listOf(),
            description = info.description ?: "",
            usedLanguages = info.languages ?: listOf(),
            preferredCountries = info.preferredCountries ?: listOf(),
            profileImageUri = info.profileImageUri ?: ""
        )
        return authRemoteDataSource.registerUser(tmp).map {
            when (it) {
                is Resource.Success -> Resource.Success(true)
                else -> Resource.Error(it.message ?: "Register failed")
            }
        }
    }

    override fun uploadImg(
        url: String,
        profilePath: String,
    ): Flow<Resource<Boolean>> {
        Log.d("PICTURE", profilePath)
        val imageFile = File(profilePath)
        val requestBody =
            imageFile.asRequestBody("image/*".toMediaTypeOrNull())

        return authRemoteDataSource.uploadImg(url, requestBody).map {
            when (it) {
                is Resource.Success -> Resource.Success(true)
                else -> Resource.Error(it.message ?: "Register failed")
            }
        }
    }


    override fun getPreSignedURL(fileName: String): Flow<Resource<String>> =
        authRemoteDataSource.getPreSigned(fileName).map {
            when (it) {
                is Resource.Success -> Resource.Success(it.data?.url ?: "")
                else -> Resource.Error(it.message ?: "PreSigned URL Failed")
            }
        }

    override fun postGoogleLogin(): Flow<Resource<UserTokenVO>> =
        authRemoteDataSource.postGoogleLogin().map {
            when (it) {
                is Resource.Success -> {
                    val mapping = it.data!!.toVO()

                    val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    val currentDate: Date = calendar.time
                    val currentTimeMillis: Long = currentDate.time

                    localPreferenceDataSource.saveAccessToken(mapping.accessToken)
                    localPreferenceDataSource.saveExpire(mapping.expiresIn + currentTimeMillis)
                    localPreferenceDataSource.saveRefreshToken(mapping.refreshToken)
                    localPreferenceDataSource.saveRefreshExpire(mapping.refreshTokenExpiresIn + currentTimeMillis)
                    localPreferenceDataSource.saveUID(mapping.id)

                    Resource.Success(it.data!!.toVO())
                }

                else -> Resource.Error(it.message ?: throw LighthouseException(null, null))
            }
        }

    override fun saveIdToken(idToken: String) {
        localPreferenceDataSource.saveIdToken(idToken)
    }
}

