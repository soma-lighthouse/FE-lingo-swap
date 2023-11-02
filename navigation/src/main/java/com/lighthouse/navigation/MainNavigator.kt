package com.lighthouse.navigation

import android.content.Context
import android.content.Intent

interface MainNavigator {
    fun navigateToMain(
        context: Context,
        newChat: Pair<String, Boolean>,
        channelId: Pair<String, String>,
        url: Pair<String, String>,
    ): Intent

    fun navigateToProfile(
        context: Context,
        userId: Pair<String, String>,
        isMe: Pair<String, Boolean>,
        isChat: Pair<String, Boolean>
    )

    fun navigateToInterest(
        context: Context,
    )

    fun navigateToCountry(
        context: Context,
        multiSelect: Pair<String, Boolean>,
    )

    fun navigateToLanguage(
        context: Context,
        position: Pair<String, Int>,
    )

    fun navigateToCamera(
        context: Context,
    ): Intent

    fun navigateToLogin(
        context: Context,
    )
}