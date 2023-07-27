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
                        spannableText.append(RichTextSpannable(it).applyRichTextStyle())
                    }
                    richText.imageRichType?.let {
                        spannableText.append(RichImageSpannable(it).setImage(context))
                    }
                }
                spannableText
            }
            return result.await()
        }
    }
}