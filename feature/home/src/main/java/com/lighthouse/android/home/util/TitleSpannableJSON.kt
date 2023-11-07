package com.lighthouse.android.home.util

import android.content.Context
import com.lighthouse.domain.entity.response.server_driven.RichText
import com.lighthouse.domain.entity.response.server_driven.RichTextType

fun getHomeTitle(context: Context): List<RichText> {
    return listOf(
        RichText(
            textRichType = RichTextType(
                text = context.getString(com.lighthouse.android.common_ui.R.string.home_title_1),
                style = listOf("bold"),
                textColor = null,
                background = null,
                size = 18.0f
            ), imageRichType = null
        ), RichText(
            textRichType = RichTextType(
                text = context.getString(com.lighthouse.android.common_ui.R.string.home_title_2),
                style = listOf("bold"),
                textColor = "#17a9a0",
                background = null,
                size = 18.0f
            ), imageRichType =
            null
        ), RichText(
            textRichType = RichTextType(
                text = context.getString(com.lighthouse.android.common_ui.R.string.home_title_3),
                style = listOf("bold"),
                textColor = null,
                background = null,
                size = 18.0f
            ), imageRichType = null
        ), RichText(
            textRichType = RichTextType(
                text = context.getString(com.lighthouse.android.common_ui.R.string.home_title_4),
                style = listOf("bold"),
                textColor = "#17a9a0",
                background = null,
                size = 18.0f
            ), imageRichType = null
        ), RichText(
            textRichType = RichTextType(
                text = context.getString(com.lighthouse.android.common_ui.R.string.home_title_5),
                style = listOf("bold"),
                textColor = null,
                background = null,
                size = 18.0f
            ), imageRichType = null
        )
    )
}
