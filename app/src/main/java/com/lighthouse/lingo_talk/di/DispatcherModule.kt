package com.lighthouse.lingo_talk.di

import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.lingo_talk.dispatcher.DispatcherProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
fun interface DispatcherModule {
    @Binds
    fun bindDispatcherProvider(provider: DispatcherProviderImpl): DispatcherProvider
}


