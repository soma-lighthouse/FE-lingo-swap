package com.lighthouse.navigation

import android.content.Context

interface MainNavigator {
    fun navigateToMain(context: Context)
    fun navigateToProfile(
        context: Context,
        userId: Pair<String, Int>,
        isMe: Pair<String, Boolean>,
    )
}