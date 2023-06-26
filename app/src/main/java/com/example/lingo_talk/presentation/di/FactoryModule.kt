package com.example.lingo_talk.presentation.di

import android.app.Application
import com.example.lingo_talk.domain.usecase.GetPostUseCase
import com.example.lingo_talk.domain.usecase.SavePostUseCase
import com.example.lingo_talk.presentation.viewModel.PostViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FactoryModule {
    @Provides
    @Singleton
    fun provideFactory(
        app: Application,
        getPostUseCase: GetPostUseCase,
        savePostUseCase: SavePostUseCase
    ): PostViewModelFactory {
        return PostViewModelFactory(getPostUseCase, savePostUseCase)
    }
}