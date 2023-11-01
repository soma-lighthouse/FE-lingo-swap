package com.lighthouse.profile.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.request.UploadInterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.ChatRepository
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.repository.ProfileRepository
import com.lighthouse.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateUserProfileUseCase,
    private val homeRepository: HomeRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    dispatcherProvider: DispatcherProvider,
    application: Application
) : BaseViewModel(dispatcherProvider, application) {

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
            profileRepository.getProfileDetail(userId).catch {
                _detail.value = handleException(it)
            }.collect {
                _detail.emit(UiState.Success(it))
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
            authRepository.getPreSignedURL(fileName).catch {
                _upload.value = handleException(it)
            }.collect {
                UiState.Success(it)
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
            authRepository.uploadImg(profileUrl!!, filePath).catch {
                _register.value = handleException(it)
            }.collect {
                _register.value = UiState.Success(it)
            }
        }
    }


    fun getMyQuestions() {
        onIO {
            profileRepository.getMyQuestions().catch {
                _detail.value = handleException(it)
            }.collect {
                Log.d("TESTING", it.toString())
                _detail.emit(UiState.Success(it))
            }
        }
    }

    fun createChannel() {
        onIO {
            chatRepository.createChannel(userId).catch {
                _detail.value = handleException(it)
            }.collect {
                _create.emit(UiState.Success(it))
            }
        }
    }

    fun saveLanguageFilter(languages: List<LanguageVO>) {
        onIO {
            homeRepository.saveLanguageVO(languages)
        }
    }

    fun getLanguageFilter() = homeRepository.getLanguageVO()

    fun getUUID() = profileRepository.getUUID()

    fun setNotification(b: Boolean) = profileRepository.setPushEnabled(b)

    fun getNotification() = profileRepository.getPushEnabled()
}