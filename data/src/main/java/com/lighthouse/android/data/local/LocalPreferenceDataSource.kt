package com.lighthouse.android.data.local

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
}