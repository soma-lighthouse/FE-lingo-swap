package com.lighthouse.android.common_ui.util

import android.content.SharedPreferences
import com.lighthouse.navigation.MainNavigator
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

sealed interface Injector {
    @EntryPoint
    @InstallIn(ActivityComponent::class)
    fun interface MainNavigatorInjector {
        fun mainNavigator(): MainNavigator
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    fun interface SharedPreferencesInjector {
        fun sharedPreferences(): SharedPreferences
    }
}