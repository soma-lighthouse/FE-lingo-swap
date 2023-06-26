package com.example.lingo_talk.data.repository

import android.util.Log
import com.example.lingo_talk.data.model.PostsItem
import com.example.lingo_talk.data.repository.datasource.PostCacheDataSource
import com.example.lingo_talk.data.repository.datasource.PostLocalDataSource
import com.example.lingo_talk.data.repository.datasource.PostRemoteDataSource
import com.example.lingo_talk.domain.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class PostRepositoryImpl(
    private val postRemoteDataSource: PostRemoteDataSource,
    private val postLocalDataSource: PostLocalDataSource,
    private val postCacheDataSource: PostCacheDataSource
) : PostRepository {
    override suspend fun getPosts(): List<PostsItem>? {
        return getMovieFromCache()
    }

    private suspend fun getMovieFromCache(): List<PostsItem>? {
        lateinit var postList: List<PostsItem>
        try {
            postList = postCacheDataSource.getPostFromCache()
        } catch (e: Exception) {
            Log.i("CACHEERROR", e.message.toString())
        }
        if (postList.size > 0) {
            return postList
        } else {
            postList = getPostFromDB()
            postCacheDataSource.savePostToCache(postList)
        }

        return postList
    }

    private suspend fun getPostFromDB(): List<PostsItem> {
        lateinit var postList: List<PostsItem>

        try {
            postList = postLocalDataSource.getPostFromDB()

        } catch (e: Exception) {
            Log.i("DBERROR", e.message.toString())
        }
        if (postList.size > 0) {
            return postList
        } else {
            postList = getPostFromAPI()
            postLocalDataSource.savePostToDB(postList)
        }

        return postList
    }

    private suspend fun getPostFromAPI(): List<PostsItem> {
        lateinit var postList: List<PostsItem>

        try {
            val response = postRemoteDataSource.getPosts()
            val body = response.body()
            if (body != null) {
                postList = body
            }
        } catch (e: Exception) {
            Log.i("APIERROR", e.message.toString())
        }
        return postList
    }

    override suspend fun savePosts(postsItem: List<PostsItem>): List<PostsItem> {
        val newPosts = getPostFromDB()
        postLocalDataSource.clearAll()
        postLocalDataSource.savePostToDB(postsItem)
        postCacheDataSource.savePostToCache(postsItem)
        return newPosts
    }
}