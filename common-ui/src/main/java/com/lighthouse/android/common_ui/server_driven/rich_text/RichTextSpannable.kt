package com.lighthouse.android.common_ui.server_driven.rich_text

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import com.lighthouse.domain.response.RichTextType

class RichTextSpannable(richText: RichTextType) : SpannableString(richText.text) {
    class Builder(private val richText: RichTextType) {
        private val spannableString = SpannableString(richText.text)

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
                val span = AbsoluteSizeSpan(convertSpToPx(it).toInt())
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

        private fun setSpan(span: Any?) {
            spannableString.setSpan(
                span,
                0,
                richText.text.length,
                SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        private fun convertSpToPx(sp: Float): Float {
            val metrics = Resources.getSystem().displayMetrics
            return sp * metrics.scaledDensity
        }

        fun build() = spannableString

    }

}