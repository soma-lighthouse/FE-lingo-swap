package com.lighthouse.lingo_swap

import android.content.Context
import com.lighthouse.android.common_ui.util.navigateActivity
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
}