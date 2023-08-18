package com.lighthouse.lingo_swap

import android.content.Context
import com.lighthouse.android.common_ui.util.buildIntent
import com.lighthouse.android.common_ui.util.navigateActivity
import com.lighthouse.auth.view.CameraActivity
import com.lighthouse.auth.view.CountryListActivity
import com.lighthouse.auth.view.InterestListActivity
import com.lighthouse.auth.view.LanguageListActivity
import com.lighthouse.navigation.MainNavigator
import com.lighthouse.profile.view.DetailProfileActivity

class MainNavigatorImpl : MainNavigator {
    override fun navigateToMain(context: Context) {
        context.navigateActivity<MainActivity>()
    }

    override fun navigateToProfile(
        context: Context,
        userId: Pair<String, Int>,
        isMe: Pair<String, Boolean>,
    ) {
        context.navigateActivity<DetailProfileActivity>(userId, isMe)
    }

    override fun navigateToInterest(context: Context) = context.buildIntent<InterestListActivity>()

    override fun navigateToCountry(context: Context, multiSelect: Pair<String, Boolean>) =
        context.buildIntent<CountryListActivity>(multiSelect)

    override fun navigateToLanguage(
        context: Context,
        selectedList: Pair<String, List<String>>,
        position: Pair<String, Int>,
    ) =
        context.buildIntent<LanguageListActivity>(selectedList, position)

    override fun navigateToCamera(context: Context) = context.buildIntent<CameraActivity>()
}