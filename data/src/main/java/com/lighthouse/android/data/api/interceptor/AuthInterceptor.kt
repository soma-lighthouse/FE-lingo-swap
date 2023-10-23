package com.lighthouse.android.data.api.interceptor

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.model.response.BaseResponse
import com.lighthouse.android.data.util.LocalKey
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
    private val remoteConfig: FirebaseRemoteConfig,
) : Interceptor {
    private val client = OkHttpClient.Builder().build()
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("TESTING URL", chain.request().url.toString())

        Log.d("TESTING ACCESS_TOKEN", getAccessToken())

        val response = if (chain.request().method != "PUT") {
            val addHeader = chain.request().newBuilder().addHeader(
                AUTH_KEY, AUTH_VALUE.format(getAccessToken())
            ).build()
            chain.proceed(addHeader)
        } else {
            chain.proceed(chain.request())
        }

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
            .url(remoteConfig.getString("LIGHTHOUSE_BASE_URL") + TOKEN_REQUEST)
            .post(body)
            .build()

        Log.d("TESTING URL", request.url.toString())

        val auth = requestRefresh(request)
        val dataJson = Gson().toJson(auth.data)
        val responseObject = JsonParser.parseString(dataJson).asJsonObject
        val token = Gson().fromJson(responseObject, TokenVO::class.java)

        Log.d("TESTING token", token.toString())
        storeToken(token.accessToken, token.refreshToken)
        storeExpire(token.expiresIn, token.refreshTokenExpiresIn)
        return token.accessToken
    }


    private fun getRefreshToken(): String {
        return localPreferenceDataSource.getString(LocalKey.REFRESH_TOKEN)
    }

    private fun getAccessToken(): String {
        return localPreferenceDataSource.getString(LocalKey.ACCESS_TOKEN)
    }

    private fun requestRefresh(request: Request): BaseResponse<TokenVO> {
        val response: Response = runBlocking {
            withContext(Dispatchers.IO) { client.newCall(request).execute() }
        }
        Log.d("TESTING response", response.body.toString())
        if (response.isSuccessful) {
            return response.getDto()
        }

        localPreferenceDataSource.clearToken()
        Thread.sleep(1000)
        throw LighthouseException(REFRESH_TOKEN_EXPIRED, REFRESH_FAILURE)
    }

    private fun storeToken(accessToken: String, refreshToken: String?) {
        localPreferenceDataSource.save(LocalKey.ACCESS_TOKEN, accessToken)
        refreshToken?.let {
            localPreferenceDataSource.save(LocalKey.REFRESH_TOKEN, refreshToken)
        }
    }

    private fun storeExpire(accessTokenExpire: Long, refreshTokenExpire: Long?) {
        localPreferenceDataSource.save(LocalKey.ACCESS_TOKEN_EXPIRE, accessTokenExpire)
        if (refreshTokenExpire != null && refreshTokenExpire > 0) {
            localPreferenceDataSource.save(LocalKey.REFRESH_TOKEN_EXPIRE, refreshTokenExpire)
        }
    }

    companion object {
        private const val TOKEN_REQUEST = "/api/v1/auth/token"
        private const val LOGIN_REQUEST = "api/v1/auth/login/google"
        private const val SIGNUP_REQUEST = "api/v1/user"

        private const val TOKEN_EXPIRED = 401

        private const val TOKEN_EMPTY = 40101
        private const val REFRESH_TOKEN_EXPIRED = 40401
        private const val ID_TOKEN_EMPTY = 40102
        private const val REFRESH_TOKEN_FAILURE = 40402

        private const val AUTH_KEY = "Authorization"
        private const val AUTH_VALUE = "Bearer %s"
        private const val REFRESH = "refreshToken"
        private const val REFRESH_FAILURE = "Refresh token failure"
    }
}