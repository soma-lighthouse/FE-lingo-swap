package com.lighthouse.android.data.local

import com.google.gson.reflect.TypeToken

interface LocalPreferenceDataSource {
    fun clearToken()

    fun save(key: String, value: String)
    fun save(key: String, value: Boolean)
    fun save(key: String, value: Long)
    fun <T> save(key: String, value: List<T>)

    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getLong(key: String): Long
    fun <T> getList(key: String, tt: TypeToken<List<T>>): List<T>

    fun clearAllData()
}