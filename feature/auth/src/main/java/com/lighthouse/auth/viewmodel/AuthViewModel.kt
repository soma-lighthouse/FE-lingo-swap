package com.lighthouse.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.constriant.LoginState
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.domain.usecase.CheckLoginStatusUseCase
import com.lighthouse.domain.usecase.GetAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val useCase: GetAuthUseCase,
    private val loginStatus: CheckLoginStatusUseCase,
    dispatcherProvider: DispatcherProvider
) : BaseViewModel(dispatcherProvider) {
    private val _loginState: MutableLiveData<LoginState> = MutableLiveData()
    val loginState: LiveData<LoginState> = _loginState

    val userId: UUID = UUID.randomUUID()
    val registerInfo = RegisterInfoVO()
    var profilePath: String? = null
    var profileUrl: String? = null

    private val _result = MutableStateFlow<UiState>(UiState.Loading)
    val result: StateFlow<UiState> = _result.asStateFlow()

    private val _upload = MutableStateFlow<Boolean>(false)
    val upload: StateFlow<Boolean> = _upload.asStateFlow()

    private val _register = MutableStateFlow(false)
    val register: StateFlow<Boolean> = _register.asStateFlow()

    fun getAccessToken() = useCase.getAccessToken()


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
        onIO {
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
        onIO {
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

    fun registerUser() {
        onIO {
            useCase.registerUser(registerInfo).catch {
                _register.value = false
            }.collect {
                when (it) {
                    is Resource.Success -> _register.value = true
                    else -> _register.value = false
                }
            }
        }
    }

    fun getPreSignedUrl(fileName: String) {
        onIO {
            useCase.getPreSignedURL(fileName).catch {
                if (it is LighthouseException) {
                    _result.value = UiState.Error(it)
                }
            }.collect {
                when (it) {
                    is Resource.Success -> _result.emit(UiState.Success(it.data!!))
                    else -> _result.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }
        }
    }

    fun uploadImg(filePath: String) {
        onIO {
            useCase.uploadImg(profileUrl!!, filePath)
                .catch {
                    _upload.value = false
                }.collect {
                    when (it) {
                        is Resource.Success -> _upload.value = true
                        else -> _upload.value = false
                    }
                }
        }
    }

    fun saveIdToken(idToken: String) {
        useCase.saveIdToken(idToken)
    }

    fun postGoogleLogin() {
        onIO {
            useCase.postGoogleLogin().catch {
                if (it is LighthouseException) {
                    _result.emit(UiState.Error(it.message))
                } else {
                    Log.e("TESTING", it.stackTraceToString())
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