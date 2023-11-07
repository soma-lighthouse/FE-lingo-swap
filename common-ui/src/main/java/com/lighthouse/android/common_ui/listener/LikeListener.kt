package com.lighthouse.android.common_ui.listener

fun interface LikeListener {
    fun updateLike(questionId: Int, like: Boolean)
}