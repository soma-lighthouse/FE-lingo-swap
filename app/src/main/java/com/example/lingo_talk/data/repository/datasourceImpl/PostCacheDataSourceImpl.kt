package com.example.lingo_talk.data.repository.datasourceImpl

import com.example.lingo_talk.data.model.PostsItem
import com.example.lingo_talk.data.repository.datasource.PostCacheDataSource

class PostCacheDataSourceImpl :  PostCacheDataSource{
    private var postList = ArrayList<PostsItem>()

    override suspend fun getPostFromCache(): List<PostsItem> {
        return postList
    }

    override suspend fun savePostToCache(posts: List<PostsItem>) {
        postList.clear()
        postList = ArrayList(posts)
    }
}