package com.lighthouse.auth.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.constriant.LoginState
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.usecase.CheckLoginStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val loginStatus: CheckLoginStatusUseCase,
    dispatcherProvider: DispatcherProvider,
    application: Application
) : BaseViewModel(dispatcherProvider, application) {
    private val _loginState: MutableLiveData<LoginState> = MutableLiveData()
    val loginState: LiveData<LoginState> = _loginState

    val userId: UUID = UUID.randomUUID()
    val registerInfo = RegisterInfoVO()
    var profilePath: String? = null
    var profileUrl: String? = null
    var filePath: String = ""

    private val _result = MutableStateFlow<UiState>(UiState.Loading)
    val result: StateFlow<UiState> = _result.asStateFlow()

    private val _upload = MutableStateFlow(false)
    val upload: StateFlow<Boolean> = _upload.asStateFlow()

    private val _register = MutableStateFlow(false)
    val register: StateFlow<Boolean> = _register.asStateFlow()

    fun getLoginStatus() {
        onIO {
            val deferred = async {
                loginStatus.invoke()
            }

            val data = deferred.await()
            Log.d("TESTING", data.toString())
            _loginState.postValue(data)
        }
    }

    fun getInterestList() {
        onIO {
            authRepository.getInterestList()
                .catch {
                    _result.value = handleException(it)
                }.collect {
                    _result.emit(UiState.Success(it))
                }
        }
    }

    fun getLanguageList() {
        onIO {
            authRepository.getLanguageList()
                .catch {
                    _result.value = handleException(it)
                }.collect {
                    _result.emit(UiState.Success(it))
                }
        }
    }


    fun getCountryList() {
        onIO {
            authRepository.getCountryList()
                .catch {
                    _result.value = handleException(it)
                }.collect {
                    _result.emit(UiState.Success(it))
                }
        }
    }

    fun registerUser() {
        onIO {
            authRepository.registerUser(registerInfo)
                .catch {
                    _register.value = false
                }.collect {
                    _register.value = it
                }
        }
    }

    fun getPreSignedUrl(fileName: String) {
        onIO {
            authRepository.getPreSignedURL(fileName)
                .catch {
                    _result.value = handleException(it)
                }.collect {
                    _result.emit(UiState.Success(it))
                }
        }
    }

    fun uploadImg(filePath: String) {
        onIO {
            authRepository.uploadImg(profileUrl!!, filePath)
                .catch {
                    _upload.value = false
                    handleException(it)
                }.collect {
                    _upload.value = it
                }
        }
    }

    fun saveIdToken(idToken: String) {
        authRepository.saveIdToken(idToken)
    }

    fun postGoogleLogin() {
        onIO {
            authRepository.postGoogleLogin()
                .onStart {
                    _result.value = UiState.Loading
                }
                .catch {
                    _result.value = handleException(it)
                }.collect {
                    _result.emit(UiState.Success(it))
                }
        }
    }

    fun clearResult() {
        _result.value = UiState.Loading
    }

}