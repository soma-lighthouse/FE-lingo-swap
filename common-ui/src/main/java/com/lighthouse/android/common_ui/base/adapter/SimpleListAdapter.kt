package com.lighthouse.android.common_ui.base.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.NativeAdBinding


class SimpleListAdapter<T : Any, B : ViewDataBinding>(
    val diffCallBack: DiffUtil.ItemCallback<T>,
    private val layoutId: Int,
    private val onBindCallback: (ViewHolder<B>, T) -> Unit,
    private val ads: Boolean = false,
    private val context: Context? = null,
) : ListAdapter<T, ViewHolder<B>>(diffCallBack) {

    private var isLoadingVisible = false

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
        private const val TYPE_AD = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> {
        val inflater = LayoutInflater.from(parent.context)

        val binding = when (viewType) {
            TYPE_ITEM -> DataBindingUtil.inflate<B>(inflater, layoutId, parent, false)
            TYPE_LOADING -> DataBindingUtil.inflate<B>(
                inflater, R.layout.progress_item, parent, false
            )

            TYPE_AD -> DataBindingUtil.inflate<B>(
                LayoutInflater.from(parent.context), R.layout.native_ad, parent, false
            )

            else -> throw IllegalArgumentException("Invalid viewType")
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
        if (getItemViewType(position) == TYPE_ITEM) {
            onBindCallback(holder, getItem(position))
            holder.onBind(getItem(position))
        } else if (getItemViewType(position) == TYPE_AD && context != null) {
            val loader =
                AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110").forNativeAd {
                    Log.d("TESTING ADS2", it.toString())
                    if (holder.binding is NativeAdBinding) {
                        populateNativeAdView(it, holder.binding.nativeAdView)
                    }

                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        val firebase = Firebase.analytics

                        val params = Bundle()
                        params.putString("error_code", adError.code.toString())
                        params.putString("error_message", adError.message)
                        params.putString("error_domain", adError.domain)
                        firebase.logEvent("ad_error", params)
                    }
                }).withNativeAdOptions(
                    NativeAdOptions.Builder().build()
                ).build()
            val request = AdRequest.Builder().build()

            loader.loadAd(request)
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.primary)
        adView.callToActionView = adView.findViewById(R.id.cta)
        adView.iconView = adView.findViewById(R.id.icon)
        adView.starRatingView = adView.findViewById(R.id.rating_bar)

        (adView.headlineView as TextView).text = nativeAd.headline

        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar)
                .rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView!!.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingVisible && position == itemCount - 1) {
            TYPE_LOADING
        } else if ((position % 11 == 0 || position == 0) && ads) {
            TYPE_AD
        } else {
            TYPE_ITEM
        }
    }

    override fun submitList(list: List<T>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}