package com.example.lingo_talk.presentation.di

import com.example.lingo_talk.presentation.adapter.PostRecyclerAdapter
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
    fun provideAdapter(): PostRecyclerAdapter {
        return PostRecyclerAdapter()
    }
}