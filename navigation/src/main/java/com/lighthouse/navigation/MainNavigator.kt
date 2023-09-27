package com.lighthouse.navigation

import android.content.Context
import android.content.Intent

interface MainNavigator {
    fun navigateToMain(
        context: Context,
        newChat: Pair<String, Boolean>,
        channelId: Pair<String, String>
    ): Intent

    fun navigateToProfile(
        context: Context,
        userId: Pair<String, String>,
        isMe: Pair<String, Boolean>,
    )

    fun navigateToInterest(
        context: Context,
        selectedList: Pair<String, HashMap<String, List<String>>>,
    ): Intent

    fun navigateToCountry(
        context: Context,
        multiSelect: Pair<String, Boolean>,
        selectedList: Pair<String, List<String>>,
    ): Intent

    fun navigateToLanguage(
        context: Context,
        selectedList: Pair<String, List<String>>,
        position: Pair<String, Int>,
    ): Intent

    fun navigateToCamera(
        context: Context,
    ): Intent

    fun navigateToLogin(
        context: Context,
    )
}