package com.lighthouse.android.common_ui.base.adapter

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.lighthouse.android.common_ui.BuildConfig
import com.lighthouse.android.common_ui.databinding.NativeAdBinding
import javax.inject.Inject

class AdViewHolder @Inject constructor(
    private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig,
    binding: NativeAdBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind() {
        val adID = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/2247696110"
        } else {
            remoteConfig.getString("AD_ID")
        }

        val loader = AdLoader.Builder(context, adID)
            .forNativeAd {
                Log.d("TESTING ADS", it.toString())
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()

        loader.loadAd(AdRequest.Builder().build())

    }
}