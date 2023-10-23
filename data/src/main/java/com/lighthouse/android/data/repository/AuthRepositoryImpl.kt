package com.lighthouse.android.data.repository

import android.util.Log
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.request.RegisterInfoDTO
import com.lighthouse.android.data.model.request.UploadInterestDTO
import com.lighthouse.android.data.repository.datasource.AuthRemoteDataSource
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.TokenVO
import com.lighthouse.domain.entity.response.vo.UserTokenVO
import com.lighthouse.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val localPreferenceDataSource: LocalPreferenceDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
) : AuthRepository {
    override fun getUserId(): String? {
        return localPreferenceDataSource.getUUID()
    }

    override fun getAccessToken(): String {
        return localPreferenceDataSource.getAccessToken() ?: ""
    }

    override fun getExpireTime(): Long {
        return localPreferenceDataSource.getExpire()
    }

    override fun getRefreshExpireTime(): Long {
        return localPreferenceDataSource.getRefreshExpire()
    }

    override fun saveAccessToken(accessToken: String, expireTime: Long) {
        localPreferenceDataSource.saveAccessToken(accessToken)
        localPreferenceDataSource.saveExpire(expireTime)
    }

    override fun getInterestList(): Flow<List<InterestVO>> =
        authRemoteDataSource.getInterestList().map {
            it.interest.map { interest ->
                interest.toVO()
            }
        }

    override fun getLanguageList(): Flow<List<LanguageVO>> =
        authRemoteDataSource.getLanguageList().map {
            it.language.map { lang ->
                lang.toVO()
            }
        }

    override fun getCountryList(): Flow<List<CountryVO>> =
        authRemoteDataSource.getCountryList().map {
            it.country.map { country ->
                country.toVO()
            }
        }

    override fun registerUser(info: RegisterInfoVO): Flow<Boolean> {
        val tmp = RegisterInfoDTO(uuid = info.uuid ?: "null",
            name = info.name ?: "null",
            birthday = info.birthday ?: "null",
            email = info.email ?: "null",
            gender = info.gender ?: "null",
            region = info.region ?: "null",
            preferredInterests = info.preferredInterests?.map {
                UploadInterestDTO(it.category, it.interests)
            } ?: listOf(),
            description = info.description ?: "",
            usedLanguages = info.languages ?: listOf(),
            preferredCountries = info.preferredCountries ?: listOf(),
            profileImageUri = info.profileImageUri ?: "")
        return authRemoteDataSource.registerUser(localPreferenceDataSource.getIdToken(), tmp).map {
            val mapping = it.toVO()
            saveTokens(mapping.tokens, mapping.uuid)
            localPreferenceDataSource.saveUserName(mapping.userName)
            true
        }
    }

    override fun uploadImg(
        url: String,
        profilePath: String,
    ): Flow<Boolean> {
        Log.d("PICTURE", profilePath)
        val imageFile = File(profilePath)
        val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())

        return authRemoteDataSource.uploadImg(url, requestBody)
    }


    override fun getPreSignedURL(fileName: String): Flow<String> =
        authRemoteDataSource.getPreSigned(fileName).map {
            it.url ?: ""
        }

    override fun postGoogleLogin(): Flow<UserTokenVO> =
        authRemoteDataSource.postGoogleLogin(localPreferenceDataSource.getIdToken()).map {
            val mapping = it.toVO()
            saveTokens(mapping.tokens, mapping.uuid)
            it.toVO()
        }

    override fun saveIdToken(idToken: String) {
        localPreferenceDataSource.saveIdToken(idToken)
    }

    private fun saveTokens(mapping: TokenVO, uuid: String) {
        Log.d("TESTING tokens", mapping.toString())

        localPreferenceDataSource.saveAccessToken(mapping.accessToken)
        localPreferenceDataSource.saveExpire(mapping.expiresIn)
        localPreferenceDataSource.saveRefreshToken(mapping.refreshToken)
        localPreferenceDataSource.saveRefreshExpire(mapping.refreshTokenExpiresIn)
        localPreferenceDataSource.saveUUID(uuid)
    }
}
