package com.lighthouse.domain.response

sealed class ContentVO {
    data class HomeTitleContent(
        val tvHomeTitle: List<RichText>
    ) : ContentVO()

    data class UserInfoTile(
        val tvProfileName: List<RichText>,
        val tvProfileIntro: List<RichText>,
        val tvProfileImg: RichText,
        val tvProfileNation: List<String>,
    ) : ContentVO()

    object UnknownContent : ContentVO()
}