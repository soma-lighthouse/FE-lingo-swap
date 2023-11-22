package com.lighthouse.android.data.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.lighthouse.domain.constriant.ErrorTypeHandling
import com.lighthouse.domain.entity.response.vo.LighthouseException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.suspendCoroutine

@Singleton
class RemoteConfigDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {
    suspend fun fetchRemoteConfig(key: String): String {
        return suspendCoroutine { continuation ->
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = remoteConfig.getString(key)
                    continuation.resumeWith(Result.success(result))
                } else {
                    continuation.resumeWith(
                        Result.failure(
                            task.exception ?: LighthouseException(
                                50001,
                                "Unknown error",
                                ErrorTypeHandling.TOAST
                            )
                        )
                    )
                }
            }
                .addOnFailureListener {
                    remoteConfig.fetchAndActivate()
                    continuation.resumeWith(
                        Result.failure(
                            it
                        )
                    )
                }
        }
    }
}