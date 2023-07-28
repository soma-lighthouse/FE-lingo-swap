package com.lighthouse.android.common_ui.server_driven.rich_text

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import com.lighthouse.domain.response.server_driven.RichImageType
import kotlinx.coroutines.coroutineScope
import java.net.URL

class RichImageSpannable(private val richImage: RichImageType) : SpannableString(richImage.url) {
    private val spannableString = SpannableString(richImage.url)

    suspend fun setImage(
        context: Context,
    ): SpannableString = coroutineScope {
        richImage.run {
            val density = Resources.getSystem().displayMetrics.density
            url?.let {
                val imageWidth = (width?.times(density) ?: 0).toInt()
                val imageHeight = (height?.times(density) ?: 0).toInt()
                val bitmap =
                    BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())

                val imageSpan = ImageSpan(context, bitmap)

                val dynamicDrawableSpan = object : DynamicDrawableSpan() {
                    override fun getDrawable(): Drawable {
                        imageSpan.drawable.setBounds(0, 0, imageWidth, imageHeight)
                        return imageSpan.drawable
                    }
                }
                spannableString.setSpan(
                    dynamicDrawableSpan,
                    0,
                    richImage.url.length,
                    SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            spannableString
        }
    }
}