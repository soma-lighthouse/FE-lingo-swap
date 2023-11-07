package com.lighthouse.android.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lighthouse.android.data.util.LocalKey
import javax.inject.Inject


class LocalPreferenceDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : LocalPreferenceDataSource {
    private val gson = Gson()

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

    override fun <T> save(key: String, value: List<T>) {
        val gson = GsonBuilder().create()
        val jsonArray = gson.toJsonTree(value).asJsonArray
        sharedPreferences.edit {
            putString(key, jsonArray.toString())
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

    override fun <T> getList(key: String, tt: TypeToken<List<T>>): List<T> {
        return gson.fromJson(sharedPreferences.getString(key, "[]"), tt.type)
    }

    override fun clearAllData() {
        sharedPreferences.edit {
            remove(LocalKey.LANGUAGE_SETTING)
            remove(LocalKey.INTEREST_SETTING)
            remove(LocalKey.COUNTRY_SETTING)
            remove(LocalKey.REGION_SETTING)
        }
    }
}