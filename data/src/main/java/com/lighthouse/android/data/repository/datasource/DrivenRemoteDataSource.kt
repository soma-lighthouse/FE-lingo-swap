package com.lighthouse.android.data.repository.datasource

import com.lighthouse.domain.response.server_driven.ViewTypeVO
import kotlinx.coroutines.flow.Flow

fun interface DrivenRemoteDataSource {
    fun getDriven(): Flow<List<ViewTypeVO>>
}