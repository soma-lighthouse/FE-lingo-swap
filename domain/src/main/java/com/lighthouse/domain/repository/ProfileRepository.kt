package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.response.vo.ProfileVO
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileDetail(userId: String): Flow<Resource<ProfileVO>>

    fun getUID(): String?
}