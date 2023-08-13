package com.lighthouse.domain.repository

import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.response.vo.ProfileVO
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileDetail(userId: Int): Flow<Resource<ProfileVO>>
}