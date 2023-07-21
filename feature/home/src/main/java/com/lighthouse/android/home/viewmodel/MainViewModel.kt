package com.lighthouse.android.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.domain.repository.DrivenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val drivenRepository: DrivenRepository
) : ViewModel() {
    fun getDrivenInfo() {
        viewModelScope.launch {
            val value = drivenRepository.getDriven()
            value.collect {
                it.forEach{
                    Log.d("TEST", it.toString())
                }
            }
        }
    }
}