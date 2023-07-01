package com.lighthouse.android.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.lighthouse.domain.usecase.GetPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPostUseCase: GetPostUseCase,
) : ViewModel() {
    fun getPost() = liveData {
        val postList = getPostUseCase.execute()
        emit(postList)
    }
}