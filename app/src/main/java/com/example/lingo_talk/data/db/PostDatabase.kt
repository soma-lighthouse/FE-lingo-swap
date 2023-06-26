package com.example.lingo_talk.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lingo_talk.data.model.PostsItem

@Database(
    entities = [PostsItem::class],
    version = 1,
    exportSchema = false
)
abstract class PostDatabase : RoomDatabase() {
    abstract fun getPostDAO(): PostDAO
}