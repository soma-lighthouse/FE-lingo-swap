package com.lighthouse.domain.entity.response.vo


data class BoardQuestionVO(
    val questionId: Int,
    val uuid: String,
    var categoryId: Int,
    val profileImageUri: String,
    val name: String,
    val region: String,
    val contents: String,
    var like: Int,
    val createAt: String,
    var clicked: Boolean
) {
    constructor() : this(0, "", 0, "", "", "", "", 0, "", false)
}