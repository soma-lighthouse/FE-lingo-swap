package com.lighthouse.domain.repository

import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.UserTokenVO
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getUserId(): String?

    fun getInterestList(): Flow<List<InterestVO>>

    fun getLanguageList(): Flow<List<LanguageVO>>

    fun getCountryList(): Flow<List<CountryVO>>

    fun registerUser(info: RegisterInfoVO): Flow<Boolean>

    fun getPreSignedURL(fileName: String): Flow<String>

    fun uploadImg(url: String, profilePath: String): Flow<Boolean>

    fun postGoogleLogin(): Flow<UserTokenVO>

    fun saveIdToken(idToken: String)

    fun getAccessToken(): String
    fun getExpireTime(): Long
    fun getRefreshExpireTime(): Long
    fun saveAccessToken(accessToken: String, expireTime: Long)
}