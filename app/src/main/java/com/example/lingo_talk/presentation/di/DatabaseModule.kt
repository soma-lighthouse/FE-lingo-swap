package com.example.lingo_talk.presentation.di

import android.app.Application
import androidx.room.Room
import com.example.lingo_talk.data.db.PostDAO
import com.example.lingo_talk.data.db.PostDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun providePostDatabase(app: Application): PostDatabase {
        return Room.databaseBuilder(app, PostDatabase::class.java, "post_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDAO(postDatabase: PostDatabase): PostDAO {
        return postDatabase.getPostDAO()
    }
}