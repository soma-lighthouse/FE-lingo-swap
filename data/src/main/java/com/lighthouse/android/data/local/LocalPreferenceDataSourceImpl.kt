package com.lighthouse.android.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.UUID
import javax.inject.Inject

class LocalPreferenceDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : LocalPreferenceDataSource {
    override fun getUUID(): String {
        val uuid = sharedPreferences.getString(UUID_ACCESS_TOKEN, null)
        return uuid ?: saveUUID()
    }

    override fun saveUUID(): String {
        val uuid = UUID.randomUUID().toString()
        sharedPreferences.edit {
            putString(UUID_ACCESS_TOKEN, uuid)
        }
        return uuid
    }

    companion object {
        const val UUID_ACCESS_TOKEN = "com.lighthouse.lingo-swap.UUID"
    }
}