package com.example.lingo_talk.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lingo_talk.data.model.PostsItem
@Dao
interface PostDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: List<PostsItem>)

    @Query("SELECT * FROM post")
    suspend fun getAllPosts(): List<PostsItem>

    @Query("DELETE FROM post")
    suspend fun deleteAllPosts()

}