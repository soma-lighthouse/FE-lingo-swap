package com.example.lingo_talk.data.repository.datasourceImpl

import android.util.Log
import com.example.lingo_talk.data.db.PostDAO
import com.example.lingo_talk.data.model.PostsItem
import com.example.lingo_talk.data.repository.datasource.PostLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostLocalDataSourceImpl(private val postDAO: PostDAO) : PostLocalDataSource {
    override suspend fun getPostFromDB(): List<PostsItem> {
        return postDAO.getAllPosts()
    }

    override suspend fun savePostToDB(posts: List<PostsItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            postDAO.insert(posts)
        }
    }

    override suspend fun clearAll() {
        CoroutineScope(Dispatchers.IO).launch {
            postDAO.deleteAllPosts()
        }
    }
}