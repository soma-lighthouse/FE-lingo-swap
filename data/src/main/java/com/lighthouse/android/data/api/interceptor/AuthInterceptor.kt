package com.lighthouse.android.data.api.interceptor

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.lighthouse.android.data.BuildConfig
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.util.getDto
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.domain.entity.response.vo.TokenVO
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

        Log.d("TESTING ACCESS_TOKEN", getAccessToken())
        val addHeader = chain.request().newBuilder().addHeader(
            AUTH_KEY, AUTH_VALUE.format(getAccessToken())
        ).build()
        val response = chain.proceed(addHeader)

        Log.d("TESTING CODE", response.code.toString())
        when (response.code) {
            TOKEN_EXPIRED -> {
                response.close()
                return reRequest(chain)
            }

            REFRESH_TOKEN_EXPIRED ->
                throw LighthouseException(
                    REFRESH_TOKEN_EXPIRED,
                    "Refresh token expired"
                ).addErrorMsg()
        }
        return response
    }

    private fun isLoginRequest(chain: Request): Boolean {
        val path = chain.url.encodedPath.substringAfter(BuildConfig.LIGHTHOUSE_BASE_URL)
        return path == LOGIN_REQUEST || path == SIGNUP_REQUEST
    }

    private fun reRequest(chain: Interceptor.Chain): Response {
        val token = getRefreshedToken()
        val request = chain.request().newBuilder().addHeader(
            AUTH_KEY, AUTH_VALUE.format(token)
        ).build()
        return chain.proceed(request)
    }

    private fun getRefreshedToken(): String {
        val body = JSONObject()
            .put(REFRESH, getRefreshToken())
            .toString()
            .toRequestBody(contentType = "application/json".toMediaType())

        Log.d("TESTING REFRESH_TOKEN", getRefreshToken())
        val request = Request.Builder()
            .url(BuildConfig.LIGHTHOUSE_BASE_URL + TOKEN_REQUEST)
            .post(body)
            .build()

        Log.d("TESTING URL", request.url.toString())

        val auth = requestRefresh(request)
        val dataJson = Gson().toJson(auth.data)
        val responseObject = JsonParser.parseString(dataJson).asJsonObject
        val token = Gson().fromJson(responseObject, TokenVO::class.java)

        Log.d("TESTING", token.toString())
        storeToken(token.accessToken, token.refreshToken)
        storeExpire(token.expiresIn, token.refreshTokenExpiresIn)
        return token.accessToken
    }


    private fun getRefreshToken(): String {
        return localPreferenceDataSource.getRefreshToken() ?: throw LighthouseException(
            TOKEN_EMPTY,
            "Token empty"
        )
    }

    private fun getAccessToken(): String {
        return localPreferenceDataSource.getAccessToken() ?: ""
    }

    private fun getIdToken(): String {
        return localPreferenceDataSource.getIdToken() ?: ""
    }

    private fun requestRefresh(request: Request): BaseResponse<TokenVO> {
        val response: Response = runBlocking {
            withContext(Dispatchers.IO) { client.newCall(request).execute() }
        }
        if (response.isSuccessful) {
            return response.getDto()
        }
        Log.d("TESTING", response.message)
        throw IllegalStateException(REFRESH_FAILURE)
    }

    private fun storeToken(accessToken: String, refreshToken: String?) {
        localPreferenceDataSource.saveAccessToken(accessToken)
        refreshToken?.let {
            localPreferenceDataSource.saveRefreshToken(refreshToken)
        }
    }

    private fun storeExpire(accessTokenExpire: Long, refreshTokenExpire: Long?) {
        localPreferenceDataSource.saveExpire(accessTokenExpire)
        refreshTokenExpire?.let {
            localPreferenceDataSource.saveRefreshExpire(refreshTokenExpire)
        }
    }

    companion object {
        private const val TOKEN_REQUEST = "api/v1/auth/token"
        private const val LOGIN_REQUEST = "api/v1/auth/login/google"
        private const val SIGNUP_REQUEST = "api/v1/user"

        private const val TOKEN_EXPIRED = 401

        private const val TOKEN_EMPTY = 40101
        private const val REFRESH_TOKEN_EXPIRED = 40401
        private const val ID_TOKEN_EMPTY = 40102

        private const val AUTH_KEY = "Authorization"
        private const val AUTH_VALUE = "Bearer %s"
        private const val REFRESH = "refreshToken"
        private const val REFRESH_FAILURE = "Refresh token failure"
    }
}