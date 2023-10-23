package com.lighthouse.android.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.lighthouse.android.data.util.LocalKey
import com.lighthouse.domain.entity.response.vo.LanguageVO
import javax.inject.Inject

class LocalPreferenceDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : LocalPreferenceDataSource {
    override fun clearToken() {
        sharedPreferences.edit {
            remove(LocalKey.ACCESS_TOKEN)
            remove(LocalKey.ACCESS_TOKEN_EXPIRE)
            remove(LocalKey.REFRESH_TOKEN)
            remove(LocalKey.REFRESH_TOKEN_EXPIRE)
            remove(LocalKey.ID_TOKEN)
        }
    }

    override fun save(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    override fun save(key: String, value: Boolean) {
        sharedPreferences.edit {
            putBoolean(key, value)
        }
    }

    override fun save(key: String, value: Long) {
        sharedPreferences.edit {
            putLong(key, value)
        }
    }

    override fun save(key: String, value: List<LanguageVO>) {
        sharedPreferences.edit {
            putString(key, Gson().toJson(value))
        }
    }

    override fun getString(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, -1)
    }

    override fun getList(key: String): List<LanguageVO> {
        val list = sharedPreferences.getString(key, null)
        return if (list != null) {
            Gson().fromJson(list, Array<LanguageVO>::class.java).toList()
        } else {
            emptyList()
        }
    }
}