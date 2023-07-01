package com.lighthouse.lingo_swap.di

import com.lighthouse.android.home.adapter.PostAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdapterModule {
    @Provides
    @Singleton
    fun provideAdapter(): PostAdapter {
        return PostAdapter()
    }
}