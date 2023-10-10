package com.lighthouse.android.common_ui.base.adapter

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.lighthouse.android.common_ui.databinding.NativeAdBinding


class AdViewHolder(
    private val context: Context,
    private val binding: NativeAdBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind() {
        val loader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
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