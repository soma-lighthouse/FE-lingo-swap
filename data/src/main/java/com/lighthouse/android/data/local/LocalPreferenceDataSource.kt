package com.lighthouse.android.data.local

import com.lighthouse.domain.entity.response.vo.LanguageVO

interface LocalPreferenceDataSource {
    fun clearToken()

    fun save(key: String, value: String)
    fun save(key: String, value: Boolean)
    fun save(key: String, value: Long)
    fun save(key: String, value: List<LanguageVO>)

    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getLong(key: String): Long
    fun getList(key: String): List<LanguageVO>
}