package com.lighthouse.android.data.repository.datasource

import kotlinx.coroutines.flow.Flow

fun interface DrivenRemoteDataSource {
    fun getDriven(): Flow<Boolean>
}