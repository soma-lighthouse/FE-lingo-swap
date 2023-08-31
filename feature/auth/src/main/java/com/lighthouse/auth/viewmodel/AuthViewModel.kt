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
    fun saveUUID() = useCase.saveUserId()

    private val _interests = MutableStateFlow<UiState>(UiState.Loading)
    val interests: StateFlow<UiState> = _interests.asStateFlow()

    private val _countries = MutableStateFlow<UiState>(UiState.Loading)
    val countries: StateFlow<UiState> = _countries.asStateFlow()

    private val _languages = MutableStateFlow<UiState>(UiState.Loading)
    val languages: StateFlow<UiState> = _languages.asStateFlow()

    fun getInterestList() {
        viewModelScope.launch {
            useCase.getInterestList()
                .catch {
                }
                .collect {
                    when (it) {
                        is Resource.Success -> _interests.emit(UiState.Success(it.data!!))
                        else -> UiState.Error(it.message ?: StringSet.error_msg)
                    }
                }

        }
    }

    fun getLanguageList() = useCase.getLanguageList()
        .map {
            when (it) {
                is Resource.Success -> UiState.Success(it.data!!)
                else -> UiState.Error(it.message ?: StringSet.error_msg)
            }
        }
        .catch {
            emit(UiState.Error(it.message ?: StringSet.error_msg))
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun getCountryList() = useCase.getCountryList()
        .map {
            when (it) {
                is Resource.Success -> UiState.Success(it.data!!)
                else -> UiState.Error(it.message ?: StringSet.error_msg)
            }
        }
        .catch {
            emit(UiState.Error(it.message ?: StringSet.error_msg))
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )

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

    fun getPreSignedURL(fileName: String) = useCase.getPreSignedURL(fileName)
        .map {
            when (it) {
                is Resource.Success -> UiState.Success(it.data!!)
                else -> UiState.Error(it.message ?: StringSet.error_msg)
            }
        }
        .catch {
            emit(UiState.Error(it.message ?: StringSet.error_msg))
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )

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