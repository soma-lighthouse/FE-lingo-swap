package com.lighthouse.android.data.local

interface LocalPreferenceDataSource {
    fun getUUID(): String
    fun saveUUID(uuid: String)
}