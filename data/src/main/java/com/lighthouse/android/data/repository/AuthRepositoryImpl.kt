package com.lighthouse.android.data.repository

import android.util.Log
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.request.RegisterInfoDTO
import com.lighthouse.android.data.repository.datasource.AuthRemoteDataSource
import com.lighthouse.android.data.util.LocalKey
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val localPreferenceDataSource: LocalPreferenceDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
) : AuthRepository {
    override fun getUserId(): String {
        return localPreferenceDataSource.getString(LocalKey.USER_ID)
    }

    override fun getAccessToken(): String {
        return localPreferenceDataSource.getString(LocalKey.ACCESS_TOKEN)
    }

    override fun getExpireTime(): Long {
        return localPreferenceDataSource.getLong(LocalKey.ACCESS_TOKEN_EXPIRE)
    }

    override fun getRefreshExpireTime(): Long {
        return localPreferenceDataSource.getLong(LocalKey.REFRESH_TOKEN_EXPIRE)
    }

    override fun saveAccessToken(accessToken: String, expireTime: Long) {
        localPreferenceDataSource.save(LocalKey.ACCESS_TOKEN, accessToken)
        localPreferenceDataSource.save(LocalKey.ACCESS_TOKEN_EXPIRE, expireTime)
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
        val tmp = RegisterInfoDTO(
            uuid = info.uuid ?: "null",
            name = info.name ?: "null",
            birthday = parseJsonToDate(info.birthday),
            email = info.email ?: "null",
            gender = parseGender(info.gender),
            region = info.region ?: "null",
            preferredInterests = info.preferredInterests ?: listOf(),
            description = info.description ?: "",
            preferredCountries = info.preferredCountries ?: listOf(),
            profileImageUri = info.profileImageUri ?: ""
        )
        return authRemoteDataSource.registerUser(
            localPreferenceDataSource.getString(LocalKey.ID_TOKEN),
            tmp
        ).map {
            val mapping = it.toVO()
            saveTokens(mapping.tokens, mapping.uuid)
            localPreferenceDataSource.save(LocalKey.USER_NAME, mapping.userName)
            true
        }
    }

    private fun parseGender(gender: String?): String {
        return when (gender) {
            "male" -> "MALE"
            "female" -> "FEMALE"
            else -> "RATHER_NOT_SAY"
        }
    }

    private fun parseJsonToDate(jsonString: String?): String {
        val json = jsonString?.trim() ?: return ""
        val year = json.substringAfter("\"year\":").substringBefore(",").toInt()
        val month = json.substringAfter("\"month\":").substringBefore(",").toInt()
        val day = json.substringAfter("\"day\":").substringBefore("}").toInt()

        return formatDate(LocalDate.of(year, month, day))
    }

    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
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
        authRemoteDataSource.postGoogleLogin(localPreferenceDataSource.getString(LocalKey.ID_TOKEN))
            .map {
                val mapping = it.toVO()
                saveTokens(mapping.tokens, mapping.uuid)
                it.toVO()
            }

    override fun saveIdToken(idToken: String) {
        localPreferenceDataSource.save(LocalKey.ID_TOKEN, idToken)
    }

    private fun saveTokens(mapping: TokenVO, uuid: String) {
        localPreferenceDataSource.save(LocalKey.ACCESS_TOKEN, mapping.accessToken)
        localPreferenceDataSource.save(LocalKey.ACCESS_TOKEN_EXPIRE, mapping.expiresIn)
        localPreferenceDataSource.save(LocalKey.REFRESH_TOKEN, mapping.refreshToken)
        localPreferenceDataSource.save(LocalKey.REFRESH_TOKEN_EXPIRE, mapping.refreshTokenExpiresIn)
        localPreferenceDataSource.save(LocalKey.USER_ID, uuid)
    }
}
