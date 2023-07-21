package com.lighthouse.domain.response

sealed class ContentVO {
    data class TitleContent(
        val title: List<RichText>,
        val detail: List<RichText>
    ) : ContentVO()

    data class ChatRoomContent(
        val userName: List<RichText>,
        val userAge: Int,
        val nation: String,
        val lastMessage: List<RichText>,
    ) : ContentVO()

    object UnknownContent : ContentVO()
}