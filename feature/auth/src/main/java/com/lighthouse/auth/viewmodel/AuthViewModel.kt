package com.lighthouse.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.usecase.GetAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val useCase: GetAuthUseCase,
) : ViewModel() {
    val registerInfo = RegisterInfoVO()
    var profilePath: String? = null
    var profileUrl: String? = null
    fun getUUID() = useCase.getUserId()
    fun saveUUID(uid: String?) = useCase.saveUserId(uid)

    private val _result = MutableStateFlow<UiState>(UiState.Loading)
    val result: StateFlow<UiState> = _result.asStateFlow()

    fun getInterestList() {
        viewModelScope.launch {
            useCase.getInterestList()
                .catch {
                    _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
                .collect {
                    when (it) {
                        is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                        else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                    }
                }

        }
    }

    fun getLanguageList() {
        viewModelScope.launch {
            useCase.getLanguageList()
                .catch {
                    _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
                .collect {
                    when (it) {
                        is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                        else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                    }
                }
        }
    }


    fun getCountryList() {
        viewModelScope.launch {
            useCase.getCountryList()
                .catch {
                    _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
                .collect {
                    when (it) {
                        is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                        else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                    }
                }
        }
    }

    fun registerUser() = useCase.registerUser(registerInfo)
        .map {
            when (it) {
                is Resource.Success -> it.data
                else -> it.message ?: StringSet.error_msg
            }
        }
        .catch {
            emit(it.message ?: StringSet.error_msg)
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun getPreSignedURL(fileName: String) {
        viewModelScope.launch {
            useCase.getPreSignedURL(fileName)
                .catch {
                    _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
                .collect {
                    when (it) {
                        is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                        else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                    }
                }
        }
    }

    fun uploadImg(filePath: String) = useCase.uploadImg(profileUrl!!, filePath)
        .map {
            when (it) {
                is Resource.Success -> it.data
                else -> it.message ?: StringSet.error_msg
            }
        }
        .catch {
            emit(it.message ?: StringSet.error_msg)
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )
}