package com.lighthouse.android.common_ui.server_driven.rich_text

import android.content.Context
import android.text.SpannableStringBuilder
import com.lighthouse.domain.response.RichText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class SpannableStringBuilderProvider {
    companion object {
        suspend fun getSpannableBuilder(
            richText: List<RichText>,
            context: Context
        ): SpannableStringBuilder {
            val result = CoroutineScope(Dispatchers.IO).async {
                val spannableText = SpannableStringBuilder()
                richText.forEach { richText ->
                    richText.textRichType?.let {
                        val spannableString = RichTextSpannable.Builder(it)
                            .setTextColor(it.textColor)
                            .setBackgroundColor(it.background)
                            .setTextSize(it.size)
                            .setTextStyle(it.style)
                            .build()
                        spannableText.append(spannableString)
                    }

                    richText.imageRichType?.let {
                        val spannableString = RichImageSpannable.Builder(it)
                            .setImage(it.url, context, it.width, it.height)
                            .build()

                        spannableText.append(spannableString)
                    }
                }
                spannableText
            }
            return result.await()
        }
    }
}