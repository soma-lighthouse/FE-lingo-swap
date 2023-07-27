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
                    val text = richText.textRichType
                    val image = richText.imageRichType
                    if (text != null) {
                        spannableText.append(RichTextSpannable(text).applyRichTextStyle())
                    } else if (image != null) {
                        spannableText.append(RichImageSpannable(image).setImage(context))
                    }
                }
                spannableText
            }
            return result.await()
        }
    }
}