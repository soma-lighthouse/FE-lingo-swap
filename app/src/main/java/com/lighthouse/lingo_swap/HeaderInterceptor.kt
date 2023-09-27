package com.lighthouse.lingo_swap

import android.os.Build
import android.util.Log
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.lighthousei18n.I18nManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val localPreferenceDataSource: LocalPreferenceDataSource,
    private val i18nManager: I18nManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        Log.d("I18n HeaderInterceptor", "intercept: ${i18nManager.getLocale().language}")
        val newRequest = request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("User-Id", localPreferenceDataSource.getUUID() ?: "1")
            .addHeader("App-Version", BuildConfig.VERSION_NAME)
            .addHeader("Device-OS", Build.VERSION.SDK_INT.toString())
            .addHeader("Accept-Language", i18nManager.getLocale().language) // "ko_KR"
            .addHeader("Timezone", i18nManager.getTimezoneId()) // "Asia/Seoul"
            .build()

        proceed(newRequest)
    }
}