package com.lighthouse.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.usecase.GetAuthUseCase
import com.lighthouse.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: GetProfileUseCase,
    private val authUseCase: GetAuthUseCase,
) : ViewModel() {

    private val _detail = MutableStateFlow<UiState>(UiState.Loading)
    val detail = _detail.asStateFlow()

    val registerInfo = RegisterInfoVO()
    var profilePath: String? = null
    var profileUrl: String? = null
    var languageList: List<String> = listOf()

    fun getProfileDetail(userId: String) {
        profileUseCase.getProfileDetail(userId)
            .catch {
                _detail.emit(UiState.Error(it.message!!))
            }
            .catch {
                when (it) {
                    is Resource.Success<*> -> _detail.emit(UiState.Success(it.data!!))
                    else -> _detail.emit(UiState.Error(it.message ?: "Error found"))
                }
            }
    }

    fun getPreSignedURL(fileName: String) {
        viewModelScope.launch {
            authUseCase.getPreSignedURL(fileName)
                .catch {
                    _detail.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
                .collect {
                    when (it) {
                        is Resource.Success -> _detail.emit(UiState.Success(it.data!!))
                        else -> _detail.emit(UiState.Error(it.message ?: StringSet.error_msg))
                    }
                }
        }
    }

    fun saveUserDetail() {
        // TODO()
    }

    fun getUUID() = profileUseCase.getUUID()
}