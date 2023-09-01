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
        userId: Pair<String, String>,
        isMe: Pair<String, Boolean>,
    ) {
        context.navigateActivity<DetailProfileActivity>(userId, isMe)
    }

    override fun navigateToInterest(
        context: Context,
        selectedList: Pair<String, HashMap<String, List<String>>>,
    ) =
        context.buildIntent<InterestListActivity>(selectedList)

    override fun navigateToCountry(
        context: Context,
        multiSelect: Pair<String, Boolean>,
        selectedList: Pair<String, List<String>>,
    ) =
        context.buildIntent<CountryListActivity>(multiSelect, selectedList)

    override fun navigateToLanguage(
        context: Context,
        selectedList: Pair<String, List<String>>,
        position: Pair<String, Int>,
        multiSelect: Pair<String, Boolean>,
    ) =
        context.buildIntent<LanguageListActivity>(selectedList, position, multiSelect)

    override fun navigateToCamera(context: Context) = context.buildIntent<CameraActivity>()
}