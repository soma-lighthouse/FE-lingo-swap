package com.lighthouse.domain.entity.response.server_driven

sealed class ContentVO {
    data class HomeTitleContent(
        val tvHomeTitle: List<RichText>,
    ) : ContentVO()

    data class UserInfoTile(
        val tvProfileName: List<RichText>,
        val tvProfileIntro: List<RichText>,
        val ivProfileImg: ImageType,
        val ivProfileNation: ImageType,
        val rvLanguage: List<List<RichText>>,
    ) : ContentVO()

    object UnknownContent : ContentVO()
}

data class ImageType(
    val image: String,
    val width: Float,
    val height: Float,
)