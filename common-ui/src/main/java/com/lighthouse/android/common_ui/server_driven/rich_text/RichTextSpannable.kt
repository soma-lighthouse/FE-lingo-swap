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
import com.lighthouse.domain.response.server_driven.RichTextType


class RichTextSpannable(private val richText: RichTextType) : SpannableString(richText.text) {
    private val spannableString = SpannableString(richText.text)

    fun applyRichTextStyle(): SpannableString {
        return richText.run {
            textColor?.let { applySpan(ForegroundColorSpan(Color.parseColor(it))) }
            background?.let { applySpan(BackgroundColorSpan(Color.parseColor(it))) }
            size?.let { applySpan(AbsoluteSizeSpan(convertSpToPx(it).toInt())) }

            style?.forEach {
                val type = when (it) {
                    "underline" -> UnderlineSpan()
                    "italic" -> StyleSpan(Typeface.ITALIC)
                    "bold" -> StyleSpan(Typeface.BOLD)
                    else -> StyleSpan(Typeface.NORMAL)
                }
                applySpan(type)
            }
            spannableString
        }
    }


    private fun applySpan(span: Any?) {
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
}