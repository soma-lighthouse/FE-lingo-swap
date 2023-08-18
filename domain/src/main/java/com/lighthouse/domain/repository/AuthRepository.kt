package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getUserId(): String

    fun saveUserId(uuid: String)

    fun getInterestList(): Flow<Resource<List<InterestVO>>>

    fun getLanguageList(): Flow<Resource<List<LanguageVO>>>

    fun getCountryList(): Flow<Resource<List<CountryVO>>>

    fun registerUser(info: RegisterInfoVO): Flow<Resource<Boolean>>

    fun getPreSignedURL(fileName: String): Flow<Resource<String>>

    fun uploadImg(url: String, profilePath: String): Flow<Resource<Boolean>>
}