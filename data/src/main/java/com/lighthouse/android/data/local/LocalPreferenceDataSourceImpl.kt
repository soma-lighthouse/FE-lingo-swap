package com.lighthouse.android.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class LocalPreferenceDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : LocalPreferenceDataSource {
    override fun getUID(): String? {
        return sharedPreferences.getString(USER_ID, null)
    }

    override fun saveUID(uid: String) {
        sharedPreferences.edit {
            putString(USER_ID, uid)
        }
    }

    override fun saveAccessToken(token: String) {
        sharedPreferences.edit {
            putString(ACCESS_TOKEN, token)
        }
    }

    override fun getAccessToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN, null)
    }

    override fun saveExpire(expireDate: Long) {
        sharedPreferences.edit {
            putLong(ACCESS_TOKEN_EXPIRE, expireDate)
        }
    }

    override fun getExpire(): Long {
        return sharedPreferences.getLong(ACCESS_TOKEN_EXPIRE, -1)
    }

    override fun saveRefreshToken(token: String) {
        sharedPreferences.edit {
            putString(REFRESH_TOKEN, token)
        }
    }

    override fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN, null)
    }

    override fun saveRefreshExpire(expireDate: Long) {
        sharedPreferences.edit {
            putLong(REFRESH_TOKEN_EXPIRE, expireDate)
        }
    }

    override fun getRefreshExpire(): Long {
        return sharedPreferences.getLong(REFRESH_TOKEN_EXPIRE, -1)
    }

    override fun saveIdToken(idToken: String) {
        sharedPreferences.edit {
            putString(ID_TOKEN, idToken)
        }
    }

    override fun getIdToken(): String? {
        return sharedPreferences.getString(ID_TOKEN, null)
    }

    companion object {
        const val USER_ID = "com.lighthouse.lingo-swap.UID"
        const val ACCESS_TOKEN = "com.lighthouse.lingo-swap.access-token"
        const val REFRESH_TOKEN = "com.lighthouse.lingo-swap.refresh-token"
        const val ACCESS_TOKEN_EXPIRE = "com.lighthouse.lingo-swap.access-token-expire"
        const val REFRESH_TOKEN_EXPIRE = "com.lighthouse.lingo-swap.refresh-token-expire"
        const val ID_TOKEN = "com.lighthouse.lingo-swap.id-token"
    }
}