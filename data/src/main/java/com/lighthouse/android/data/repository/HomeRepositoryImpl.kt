package com.lighthouse.android.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.api.paging.HomePagingSource
import com.lighthouse.android.data.repository.datasourceimpl.NetworkResponse
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.response.dto.ProfileVO
import kotlinx.coroutines.flow.Flow

class HomeRepositoryImpl(
    private val api: HomeApiService,
) : HomeRepository, NetworkResponse() {
    override fun getMatchedUser(): Flow<PagingData<ProfileVO>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false)
        ) {
            HomePagingSource(api)
        }.flow
    }
}