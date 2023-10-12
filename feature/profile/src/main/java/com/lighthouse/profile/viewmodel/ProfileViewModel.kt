package com.lighthouse.profile.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.request.UploadInterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.usecase.GetAuthUseCase
import com.lighthouse.domain.usecase.GetLanguageFilterUseCase
import com.lighthouse.domain.usecase.GetMyQuestionsUseCase
import com.lighthouse.domain.usecase.GetProfileUseCase
import com.lighthouse.domain.usecase.ManageChannelUseCase
import com.lighthouse.domain.usecase.SaveLanguageFilterUseCase
import com.lighthouse.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: GetProfileUseCase,
    private val myQuestion: GetMyQuestionsUseCase,
    private val authUseCase: GetAuthUseCase,
    private val saveLanguageFilterUseCase: SaveLanguageFilterUseCase,
    private val getLanguageFilterUseCase: GetLanguageFilterUseCase,
    private val updateProfileUseCase: UpdateUserProfileUseCase,
    private val manageChannelUseCase: ManageChannelUseCase,
    dispatcherProvider: DispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    private val _detail = MutableStateFlow<UiState>(UiState.Loading)
    val detail = _detail.asStateFlow()

    private val _upload = MutableStateFlow<UiState>(UiState.Loading)
    val upload = _upload.asStateFlow()

    private val _register = MutableStateFlow<UiState>(UiState.Loading)
    val register = _register.asStateFlow()

    private val _create = MutableStateFlow<UiState>(UiState.Loading)
    val create = _create.asStateFlow()

    private var _languageList: MutableLiveData<List<LanguageVO>> = MutableLiveData(listOf())
    var languageList: LiveData<List<LanguageVO>> = _languageList

    var registerInfo = RegisterInfoVO()
    var profilePath: String? = null
    var profileUrl: String? = null
    var userId: String = ""
    var isMe: Boolean = false
    var chat: Boolean = false
    var editMode = false
    var imageUri: Uri? = null
    var description = ""
    var filePath: String = ""

    var interestList = listOf<UploadInterestVO>()
    var interestListCode = listOf<UploadInterestVO>()

    var selectedCountryName = listOf<String>()
    var selectedCountryCode = listOf<String>()

    fun getProfileDetail(userId: String) {
        onIO {
            Log.d("TESTING", userId)
            profileUseCase.getProfileDetail(userId).catch {
                _detail.value = handleException(it)
            }.collect {
                when (it) {
                    is Resource.Success<*> -> _detail.emit(UiState.Success(it.data!!))

                    else -> _detail.emit(UiState.Error(it.message ?: "Error found"))
                }
            }
        }
    }

    fun setList(profileVO: ProfileVO) {
        selectedCountryCode = profileVO.countries.map { it.code }
        selectedCountryName = profileVO.countries.map { it.name }
        interestList = profileVO.interests.map {
            UploadInterestVO(category = it.category.code, interests = it.interests.map { it.code })
        }
        _languageList.value = profileVO.languages
    }

    fun updateLanguageList(list: List<LanguageVO>) {
        _languageList.postValue(list)
    }

    fun getPreSignedUrl(fileName: String) {
        onIO {
            authUseCase.getPreSignedURL(fileName).catch {
                _upload.value = handleException(it)
            }.collect {
                when (it) {
                    is Resource.Success -> _upload.emit(UiState.Success(it.data!!))
                    else -> _upload.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }
        }
    }

    fun saveUserDetail() {
        registerInfo.profileImageUri?.let {
            val path = extractPath(it)
            registerInfo.profileImageUri = path
        }

        val newProfile = RegisterInfoVO(
            profileImageUri = profilePath,
            description = description,
            preferredInterests = interestListCode,
            languages = languageList.value?.map {
                mapOf("code" to it.code, "level" to it.level)
            },
            preferredCountries = selectedCountryCode
        )

        Log.d("TESTING", newProfile.toString())

        onIO {
            try {
                Log.d("TESTING DETAIL", "${registerInfo}, ${newProfile}")
                val result = updateProfileUseCase.invoke(registerInfo, newProfile)
                _register.emit(UiState.Success(result))
            } catch (e: Exception) {
                _register.value = handleException(e)
            }
        }
    }

    private fun extractPath(profilePath: String): String? {
        val regex = Regex("/${getUUID()}.*")
        val matchResult = regex.find(profilePath)
        return matchResult?.value
    }

    fun uploadImg(filePath: String) {
        onIO {
            authUseCase.uploadImg(profileUrl!!, filePath).catch {
                _register.value = handleException(it)
            }.collect {
                when (it) {
                    is Resource.Success -> _register.value = UiState.Success(it.data!!)
                    else -> _register.value = UiState.Error(it.message ?: StringSet.error_msg)
                }
            }
        }
    }


    fun getMyQuestions() {
        onIO {
            myQuestion.invoke().catch {
                _detail.value = handleException(it)
            }.collect {
                Log.d("TESTING", it.toString())
                when (it) {
                    is Resource.Success<*> -> _detail.emit(UiState.Success(it.data!!))
                    else -> _detail.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }
        }
    }

    fun createChannel() {
        onIO {
            manageChannelUseCase.createChannel(userId).catch {
                _detail.value = handleException(it)
            }.collect {
                when (it) {
                    is Resource.Success<*> -> _create.emit(UiState.Success(it.data!!))
                    else -> _detail.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }
        }
    }

    fun saveLanguageFilter(languages: List<LanguageVO>) {
        onIO {
            saveLanguageFilterUseCase.invoke(languages)
        }
    }

    fun getLanguageFilter() = getLanguageFilterUseCase.invoke()

    fun getUUID() = profileUseCase.getUUID()

    fun setNotification(b: Boolean) = profileUseCase.setNotification(b)

    fun getNotification() = profileUseCase.getNotification()

    fun resetDetail() {
        _detail.value = UiState.Loading
    }

}