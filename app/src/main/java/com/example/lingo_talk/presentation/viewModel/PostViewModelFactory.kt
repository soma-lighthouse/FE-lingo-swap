package com.example.lingo_talk.presentation.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lingo_talk.domain.usecase.GetPostUseCase
import com.example.lingo_talk.domain.usecase.SavePostUseCase

class PostViewModelFactory(
    private val getPostUseCase: GetPostUseCase,
    private val savePostUseCase: SavePostUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(
                getPostUseCase,
                savePostUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}