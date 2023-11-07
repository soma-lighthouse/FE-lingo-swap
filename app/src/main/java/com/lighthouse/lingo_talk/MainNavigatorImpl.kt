package com.lighthouse.lingo_talk

import android.content.Context
import android.content.Intent
import com.lighthouse.android.common_ui.util.buildIntent
import com.lighthouse.android.common_ui.util.navigateActivity
import com.lighthouse.auth.AuthActivity
import com.lighthouse.auth.view.CameraActivity
import com.lighthouse.auth.view.CountryListActivity
import com.lighthouse.auth.view.InterestListActivity
import com.lighthouse.auth.view.LanguageListActivity
import com.lighthouse.navigation.MainNavigator
import com.lighthouse.profile.view.DetailProfileActivity

class MainNavigatorImpl : MainNavigator {
    override fun navigateToMain(
        context: Context,
        newChat: Pair<String, Boolean>,
        channelId: Pair<String, String>,
        url: Pair<String, String>,
    ): Intent {
        val intent = context.buildIntent<MainActivity>(newChat, channelId, url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return intent
    }

    override fun navigateToProfile(
        context: Context,
        userId: Pair<String, String>,
        isMe: Pair<String, Boolean>,
        isChat: Pair<String, Boolean>
    ) {
        context.navigateActivity<DetailProfileActivity>(userId, isMe, isChat)
    }

    override fun navigateToInterest(
        context: Context,
    ) =
        context.navigateActivity<InterestListActivity>()

    override fun navigateToCountry(
        context: Context,
        multiSelect: Pair<String, Boolean>,
    ) =
        context.navigateActivity<CountryListActivity>(multiSelect)

    override fun navigateToLanguage(
        context: Context,
        isRegister: Pair<String, Boolean>
    ) =
        context.navigateActivity<LanguageListActivity>(isRegister)

    override fun navigateToLogin(context: Context) =
        context.navigateActivity<AuthActivity>()

    override fun navigateToCamera(context: Context) = context.buildIntent<CameraActivity>()
}