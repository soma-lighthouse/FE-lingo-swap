package com.lighthouse.android.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.lighthouse.domain.entity.response.vo.LanguageVO
import javax.inject.Inject

class LocalPreferenceDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : LocalPreferenceDataSource {
    override fun getUUID(): String? {
        return sharedPreferences.getString(USER_ID, null)
    }

    override fun saveUUID(uuid: String) {
        sharedPreferences.edit {
            putString(USER_ID, uuid)
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

    override fun clearToken() {
        sharedPreferences.edit {
            remove(ACCESS_TOKEN)
            remove(ACCESS_TOKEN_EXPIRE)
            remove(REFRESH_TOKEN)
            remove(REFRESH_TOKEN_EXPIRE)
            remove(ID_TOKEN)
        }
    }

    override fun saveLanguageSetting(language: List<LanguageVO>) {
        sharedPreferences.edit {
            putString(LANGUAGE_SETTING, Gson().toJson(language))
        }
    }

    override fun getLanguageSetting(): List<LanguageVO> {
        val language = sharedPreferences.getString(LANGUAGE_SETTING, null)
        return if (language != null) {
            Gson().fromJson(language, Array<LanguageVO>::class.java).toList()
        } else {
            emptyList()
        }
    }

    override fun saveCurrentRegion(key: String, value: String?) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    override fun getCurrentRegion(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun saveUserName(name: String) {
        sharedPreferences.edit {
            putString(USER_NAME, name)
        }
    }

    override fun getUserName(): String? {
        return sharedPreferences.getString(USER_NAME, null)
    }

    override fun getIfFilterUpdated(): Boolean {
        return sharedPreferences.getBoolean("filter_updated", false)
    }

    override fun saveIfFilterUpdated(updated: Boolean) {
        sharedPreferences.edit {
            putBoolean("filter_updated", updated)
        }
    }

    companion object {
        const val USER_ID = "com.lighthouse.lingo-swap.UUID"
        const val USER_NAME = "com.lighthouse.lingo-swap.USER_NAME"
        const val ACCESS_TOKEN = "com.lighthouse.lingo-swap.access-token"
        const val REFRESH_TOKEN = "com.lighthouse.lingo-swap.refresh-token"
        const val ACCESS_TOKEN_EXPIRE = "com.lighthouse.lingo-swap.access-token-expire"
        const val REFRESH_TOKEN_EXPIRE = "com.lighthouse.lingo-swap.refresh-token-expire"
        const val ID_TOKEN = "com.lighthouse.lingo-swap.id-token"
        const val LANGUAGE_SETTING = "com.lighthouse.lingo-swap.language-setting"
    }
}