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
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.usecase.GetAuthUseCase
import com.lighthouse.domain.usecase.GetLanguageFilterUseCase
import com.lighthouse.domain.usecase.GetMyQuestionsUseCase
import com.lighthouse.domain.usecase.GetProfileUseCase
import com.lighthouse.domain.usecase.ManageChannelUseCase
import com.lighthouse.domain.usecase.SaveLanguageFilterUseCase
import com.lighthouse.domain.usecase.TestUseCase
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
    private val test: TestUseCase,
    dispatcherProvider: DispatcherProvider
) : BaseViewModel(dispatcherProvider) {

    private val _detail = MutableStateFlow<UiState>(UiState.Loading)
    val detail = _detail.asStateFlow()

    private val _upload = MutableStateFlow<UiState>(UiState.Loading)
    val upload = _upload.asStateFlow()

    private val _register = MutableStateFlow<UiState>(UiState.Loading)
    val register = _register.asStateFlow()

    private var _languageList: MutableLiveData<List<LanguageVO>> = MutableLiveData(listOf())
    var languageList: LiveData<List<LanguageVO>> = _languageList

    val registerInfo = RegisterInfoVO()
    var profilePath: String? = null
    var profileUrl: String? = null
    var userId: String = ""
    var isMe: Boolean = false
    var editMode = false
    var imageUri: Uri? = null
    var description = ""

    var interestList = listOf<UploadInterestVO>()
    var interestListCode = listOf<UploadInterestVO>()

    var selectedCountryName = listOf<String>()
    var selectedCountryCode = listOf<String>()

    fun getProfileDetail(userId: String) {
        onIO {
            Log.d("TESTING", userId)
            profileUseCase.getProfileDetail(userId).catch {
                if (it is LighthouseException) {
                    _detail.value = UiState.Error(it)
                }
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
        _languageList.value = list
    }

    fun getPreSignedUrl(fileName: String) {
        onIO {
            authUseCase.getPreSignedURL(fileName).catch {
                if (it is LighthouseException) {
                    _upload.value = UiState.Error(it)
                }
            }.collect {
                when (it) {
                    is Resource.Success -> _upload.emit(UiState.Success(it.data!!))
                    else -> _upload.emit(UiState.Error(it.message ?: StringSet.error_msg))
                }
            }
        }
    }

    fun saveUserDetail() {
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
            updateProfileUseCase.invoke(registerInfo, newProfile)
        }
    }

    private fun InterestVO.toUpload() =
        UploadInterestVO(category = this.category.code, interests = this.interests.map { it.code })

    fun uploadImg(filePath: String) {
        onIO {
            authUseCase.uploadImg(profileUrl!!, filePath).catch {
                if (it is LighthouseException) {
                    _upload.value = UiState.Error(it)
                }
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
                if (it is LighthouseException) {
                    _detail.value = UiState.Error(it)
                } else {
                    Log.e("TESTING", it.stackTraceToString())
                }
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
                if (it is LighthouseException) {
                    _detail.value = UiState.Error(it)
                }
            }.collect {
                when (it) {
                    is Resource.Success<*> -> _detail.emit(UiState.Success(it.data!!))
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


}


