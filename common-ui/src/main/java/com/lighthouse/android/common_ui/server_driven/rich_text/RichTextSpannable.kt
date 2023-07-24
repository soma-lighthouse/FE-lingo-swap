package com.lighthouse.android.common_ui.server_driven.rich_text

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import com.lighthouse.domain.response.RichText
import kotlinx.coroutines.coroutineScope
import java.net.URL

class RichTextSpannable(richText: RichText) : SpannableString(richText.text ?: " ") {
    class Builder(private val richText: RichText) {
        private val spannableString = SpannableString(richText.text ?: " ")

        fun setTextColor(color: String?): Builder {
            color?.let {
                val span = ForegroundColorSpan(Color.parseColor(it))
                setSpan(span)
            }
            return this
        }

        fun setBackgroundColor(color: String?): Builder {
            color?.let {
                val span = BackgroundColorSpan(Color.parseColor(it))
                setSpan(span)
            }
            return this
        }

        fun setTextSize(size: Float?): Builder {
            size?.let {
                val span = RelativeSizeSpan(it)
                setSpan(span)
            }
            return this
        }

        fun setTextStyle(style: List<String>?): Builder {
            style?.forEach {
                val type = when (it) {
                    "underline" -> UnderlineSpan()
                    "italic" -> StyleSpan(Typeface.ITALIC)
                    "bold" -> StyleSpan(Typeface.BOLD)
                    else -> StyleSpan(Typeface.NORMAL)
                }
                setSpan(type)
            }
            return this
        }

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
                    1,
                    SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            this@Builder
        }

        private fun setSpan(span: Any?) {
            spannableString.setSpan(
                span,
                0,
                richText.text?.length ?: 0,
                SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        fun build() = spannableString

    }

}