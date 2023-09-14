package com.lighthouse.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.constriant.LoginState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.domain.usecase.CheckLoginStatusUseCase
import com.lighthouse.domain.usecase.GetAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val useCase: GetAuthUseCase,
    private val loginStatus: CheckLoginStatusUseCase,
) : ViewModel() {
    private val _loginState: MutableLiveData<LoginState> = MutableLiveData()
    val loginState: LiveData<LoginState> = _loginState

    val userId: UUID = UUID.randomUUID()
    val registerInfo = RegisterInfoVO()
    var profilePath: String? = null
    var profileUrl: String? = null

    fun getAccessToken() = useCase.getAccessToken()
    fun getLoginStatus() {
        viewModelScope.launch {
            val deferred = async {
                loginStatus.invoke()
            }

            val data = deferred.await()
            Log.d("TESTING", data.toString())
            _loginState.postValue(data)
        }
    }


    private val _result = MutableStateFlow<UiState>(UiState.Loading)
    val result: StateFlow<UiState> = _result.asStateFlow()

    fun getInterestList() {
        viewModelScope.launch {
            useCase.getInterestList().catch {
                _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
            }.collect {
                when (it) {
                    is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                    else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }

        }
    }

    fun getLanguageList() {
        viewModelScope.launch {
            useCase.getLanguageList().catch {
                _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
            }.collect {
                when (it) {
                    is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                    else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }
        }
    }


    fun getCountryList() {
        viewModelScope.launch {
            useCase.getCountryList().catch {
                _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
            }.collect {
                when (it) {
                    is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                    else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }
        }
    }

    fun registerUser() = useCase.registerUser(registerInfo).map {
        when (it) {
            is Resource.Success -> it.data
            else -> it.message ?: StringSet.error_msg
        }
    }.catch {
        emit(it.message ?: StringSet.error_msg)
    }.stateIn(
        scope = viewModelScope,
        initialValue = UiState.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun getPreSignedURL(fileName: String) {
        viewModelScope.launch {
            useCase.getPreSignedURL(fileName).catch {
                _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
            }.collect {
                when (it) {
                    is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                    else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }
        }
    }

    fun uploadImg(filePath: String) = useCase.uploadImg(profileUrl!!, filePath).map {
        when (it) {
            is Resource.Success -> it.data
            else -> it.message ?: StringSet.error_msg
        }
    }.catch {
        emit(it.message ?: StringSet.error_msg)
    }.stateIn(
        scope = viewModelScope,
        initialValue = UiState.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun saveIdToken(idToken: String) {
        useCase.saveIdToken(idToken)
    }

    fun postGoogleLogin() {
        viewModelScope.launch {
            useCase.postGoogleLogin().catch {
                if (it is LighthouseException) {
                    _result.emit(UiState.Error(it.message))
                    _result.emit(UiState.Loading)
                }
            }.collect {
                when (it) {
                    is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                    else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }
        }
    }

}