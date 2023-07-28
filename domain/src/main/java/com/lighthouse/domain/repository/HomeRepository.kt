package com.lighthouse.domain.repository

import androidx.paging.PagingData
import com.lighthouse.domain.response.dto.ProfileVO
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getMatchedUser(): Flow<PagingData<ProfileVO>>
}