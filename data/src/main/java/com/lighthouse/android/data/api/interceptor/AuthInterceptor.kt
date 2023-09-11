package com.lighthouse.android.data.api.interceptor

import com.lighthouse.android.data.BuildConfig
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.util.getDto
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.domain.entity.response.vo.UserTokenVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val localPreferenceDataSource: LocalPreferenceDataSource,
) : Interceptor {
    private val client = OkHttpClient.Builder().build()
    override fun intercept(chain: Interceptor.Chain): Response {
        if (isLoginRequest(chain.request())) {
            val addHeader = chain.request().newBuilder().addHeader(
                AUTH_KEY, AUTH_VALUE.format(getIdToken())
            ).build()

            return chain.proceed(addHeader)
        }

        val addHeader = chain.request().newBuilder().addHeader(
            AUTH_KEY, AUTH_VALUE.format(getAccessToken())
        ).build()
        val response = chain.proceed(addHeader)

        when (response.code) {
            TOKEN_EXPIRED -> {
                response.close()
                return reRequest(chain)
            }
        }

        return response
    }

    private fun isLoginRequest(chain: Request): Boolean {
        val path = chain.url.encodedPath.substringAfter(BuildConfig.LIGHTHOUSE_BASE_URL)
        return path == LOGIN_REQUEST
    }

    private fun reRequest(chain: Interceptor.Chain): Response {
        val token = getRefreshedToken()
        val request = chain.request().newBuilder().addHeader(
            AUTH_KEY, AUTH_VALUE.format(token)
        ).build()
        return chain.proceed(request)
    }

    private fun getRefreshedToken() {
        val body = JSONObject()
            .put(REFRESH, getRefreshToken())
            .toString()
            .toRequestBody(contentType = "application/json".toMediaType())


        val request = Request.Builder()
            .url(BuildConfig.LIGHTHOUSE_BASE_URL + TOKEN_REQUEST)
            .post(body)
            .addHeader(AUTH_KEY, getAccessToken())
            .build()

        val auth = requestRefresh(request)
        storeToken(auth.accessToken, auth.refreshToken)
        storeExpire(auth.expiresIn, auth.refreshTokenExpiresIn)
    }

    private fun getRefreshToken(): String {
        return localPreferenceDataSource.getRefreshToken() ?: throw LighthouseException(
            TOKEN_EMPTY,
            "Token empty"
        )
    }

    private fun getAccessToken(): String {
        return localPreferenceDataSource.getRefreshToken() ?: throw LighthouseException(
            TOKEN_EMPTY,
            "Token empty"
        )
    }

    private fun getIdToken(): String {
        return localPreferenceDataSource.getIdToken() ?: throw LighthouseException(
            ID_TOKEN_EMPTY,
            "Id Token empty"
        )
    }

    private fun requestRefresh(request: Request): UserTokenVO {
        val response: Response = runBlocking {
            withContext(Dispatchers.IO) { client.newCall(request).execute() }
        }
        if (response.isSuccessful) {
            return response.getDto()
        }
        throw IllegalStateException(REFRESH_FAILURE)
    }

    private fun storeToken(accessToken: String, refreshToken: String) {
        localPreferenceDataSource.saveAccessToken(accessToken)
        localPreferenceDataSource.saveRefreshToken(refreshToken)
    }

    private fun storeExpire(accessTokenExpire: Long, refreshTokenExpire: Long) {
        localPreferenceDataSource.saveExpire(accessTokenExpire)
        localPreferenceDataSource.saveRefreshExpire(refreshTokenExpire)
    }

    companion object {
        private const val TOKEN_REQUEST = "/api/v1/auth/token"
        private const val LOGIN_REQUEST = "/api/v1/auth/login/google"

        private const val TOKEN_EXPIRED = 401

        private const val TOKEN_EMPTY = 40101
        private const val ID_TOKEN_EMPTY = 40102

        private const val AUTH_KEY = "Authorization"
        private const val AUTH_VALUE = "Bearer %s"
        private const val REFRESH = "refresh_token"
        private const val REFRESH_FAILURE = "Refresh token failure"
    }
}