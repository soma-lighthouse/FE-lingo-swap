package com.lighthouse.domain.repository

import com.lighthouse.domain.response.server_driven.ViewTypeVO
import kotlinx.coroutines.flow.Flow

fun interface DrivenRepository {
    fun getDriven(): Flow<List<ViewTypeVO>>
}