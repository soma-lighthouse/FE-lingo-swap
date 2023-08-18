package com.lighthouse.navigation

import android.content.Context
import android.content.Intent

interface MainNavigator {
    fun navigateToMain(context: Context)
    fun navigateToProfile(
        context: Context,
        userId: Pair<String, Int>,
        isMe: Pair<String, Boolean>,
    )

    fun navigateToInterest(
        context: Context,
    ): Intent

    fun navigateToCountry(
        context: Context,
        multiSelect: Pair<String, Boolean>,
    ): Intent

    fun navigateToLanguage(
        context: Context,
        selectedList: Pair<String, List<String>>,
        position: Pair<String, Int>,
    ): Intent

    fun navigateToCamera(
        context: Context,
    ): Intent
}