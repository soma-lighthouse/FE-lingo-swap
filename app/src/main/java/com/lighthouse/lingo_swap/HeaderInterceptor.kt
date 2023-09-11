package com.lighthouse.lingo_swap

import android.os.Build
import com.lighthouse.android.data.local.LocalPreferenceDataSource
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val localPreferenceDataSource: LocalPreferenceDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest = request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("User-Id", localPreferenceDataSource.getUID() ?: "1")
            .addHeader("App-Version", BuildConfig.VERSION_NAME)
            .addHeader("Device-OS", Build.VERSION.SDK_INT.toString())
            .addHeader("Language", Locale.getDefault().language)
            .addHeader("Region", Locale.getDefault().country)
            .addHeader("Timezone", TimeZone.getDefault().id)
            .build()

        proceed(newRequest)
    }
}