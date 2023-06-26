package com.example.lingo_talk.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.lingo_talk.data.model.PostsItem
import com.example.lingo_talk.domain.usecase.GetPostUseCase
import com.example.lingo_talk.domain.usecase.SavePostUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel(
    private val getPostUseCase: GetPostUseCase,
    private val savePostUseCase: SavePostUseCase
) : ViewModel() {
    fun getPost() = liveData {
        val postList = getPostUseCase.execute()
        emit(postList)
    }

    fun savePost(post: List<PostsItem>) = viewModelScope.launch(Dispatchers.IO) {
        savePostUseCase.execute(post)
    }
}