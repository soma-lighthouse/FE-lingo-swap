package com.lighthouse.android.data.local

import com.lighthouse.domain.entity.response.vo.LanguageVO

interface LocalPreferenceDataSource {
    fun getUUID(): String?
    fun saveUUID(uid: String)

    fun saveAccessToken(token: String)
    fun getAccessToken(): String?

    fun saveExpire(expireDate: Long)
    fun getExpire(): Long

    fun saveRefreshToken(token: String)
    fun getRefreshToken(): String?

    fun saveRefreshExpire(expireDate: Long)
    fun getRefreshExpire(): Long

    fun saveIdToken(idToken: String)
    fun getIdToken(): String?

    fun clearToken()

    fun saveLanguageSetting(language: List<LanguageVO>)

    fun getLanguageSetting(): List<LanguageVO>

    fun saveCurrentRegion(key: String, value: String?)
    fun getCurrentRegion(key: String): String?

    fun saveUserName(name: String)
    fun getUserName(): String?

    fun getIfFilterUpdated(): Boolean
    fun saveIfFilterUpdated(updated: Boolean)

    fun getPushEnabled(): Boolean
    fun setPushEnabled(enabled: Boolean)
}