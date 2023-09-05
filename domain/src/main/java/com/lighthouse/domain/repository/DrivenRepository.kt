package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.TestVO
import kotlinx.coroutines.flow.Flow

fun interface DrivenRepository {
    fun getDriven(): Flow<Resource<TestVO>>
}