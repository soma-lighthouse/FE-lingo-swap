package com.lighthouse.android.data.repository

import android.util.Log
import com.lighthouse.android.data.repository.datasource.IntroRemoteDataSource
import com.lighthouse.domain.repository.IntroRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.lang.Exception

class IntroRepositoryImpl(
    private val introRemoteDataSource: IntroRemoteDataSource
) : IntroRepository {
    override suspend fun getIntro(): String {
        val deferred1 = CoroutineScope(Dispatchers.IO).async(start = CoroutineStart.LAZY) {
            introRemoteDataSource.getIntro()
        }
        return try {
            deferred1.await()
        } catch (e: Exception) {
            Log.e("MYTAG", "Error while fetching intro: ${e.message}")
            "Error occurred while fetching intro."
        }
    }


}