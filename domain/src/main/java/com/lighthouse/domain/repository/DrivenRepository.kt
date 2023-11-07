package com.lighthouse.domain.repository

import kotlinx.coroutines.flow.Flow

fun interface DrivenRepository {
    fun getDriven(): Flow<Boolean>
}