package com.lighthouse.android.home.util

import com.lighthouse.domain.response.server_driven.RichText
import com.lighthouse.domain.response.server_driven.RichTextType

val homeTitle = listOf(
    RichText(
        textRichType = RichTextType(
            text = "원하는 상대의 ",
            style = listOf("bold"),
            textColor = null,
            background = null,
            size = 18.0f
        ), imageRichType = null
    ), RichText(
        textRichType = RichTextType(
            text = "프로필 ", style = listOf("bold"), textColor = "#17a9a0",
            background = null,
            size = 18.0f
        ), imageRichType =
        null
    ), RichText(
        textRichType = RichTextType(
            text = "을 찾아 ",
            style = listOf("bold"),
            textColor = null,
            background = null,
            size = 18.0f
        ), imageRichType = null
    ), RichText(
        textRichType = RichTextType(
            text = "대화 ", style = listOf("bold"), textColor = "#17a9a0",
            background = null,
            size = 18.0f
        ), imageRichType = null
    ), RichText(
        textRichType = RichTextType(
            text = "해 보세요 !",
            style = listOf("bold"),
            textColor = null,
            background = null,
            size = 18.0f
        ), imageRichType = null
    )
)
