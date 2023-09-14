package com.lighthouse.domain.usecase

import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.repository.AuthRepository
import javax.inject.Inject

class GetAuthUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    fun getUserId() = repository.getUserId()

    fun getInterestList() = repository.getInterestList()

    fun getLanguageList() = repository.getLanguageList()

    fun getCountryList() = repository.getCountryList()

    fun registerUser(info: RegisterInfoVO) = repository.registerUser(info)

    fun getPreSignedURL(fileName: String) = repository.getPreSignedURL(fileName)

    fun uploadImg(url: String, profilePath: String) = repository.uploadImg(url, profilePath)

    fun postGoogleLogin() = repository.postGoogleLogin()

    fun saveIdToken(idToken: String) = repository.saveIdToken(idToken)

    fun getAccessToken() = repository.getAccessToken()
}