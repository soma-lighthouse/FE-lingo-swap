package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.InterestVO
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getUserId(): String

    fun saveUserId(uuid: String)

    fun getInterestList(): Flow<Resource<List<InterestVO>>>
}