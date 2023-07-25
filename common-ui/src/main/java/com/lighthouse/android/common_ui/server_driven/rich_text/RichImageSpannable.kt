package com.lighthouse.android.common_ui.server_driven.rich_text

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import com.lighthouse.domain.response.RichImageType
import kotlinx.coroutines.coroutineScope
import java.net.URL

class RichImageSpannable(richImage: RichImageType) : SpannableString(richImage.url ?: " ") {
    class Builder(private val richImage: RichImageType) {
        private val spannableString = SpannableString(richImage.url ?: " ")

        suspend fun setImage(
            url: String?,
            context: Context,
            width: Float?,
            height: Float?
        ): Builder = coroutineScope {
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
                    richImage.url?.length ?: 0,
                    SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            this@Builder
        }

        fun build() = spannableString
    }
}