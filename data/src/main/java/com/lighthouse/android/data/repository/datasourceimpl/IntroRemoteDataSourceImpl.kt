package com.lighthouse.android.data.repository.datasourceimpl

import com.lighthouse.android.data.api.IntroAPIService
import com.lighthouse.android.data.repository.datasource.IntroRemoteDataSource

class IntroRemoteDataSourceImpl(
    private val api: IntroAPIService
) : IntroRemoteDataSource {
    override suspend fun getIntro(): String {
//        return api.getIntro()
        return "Test"
    }
}