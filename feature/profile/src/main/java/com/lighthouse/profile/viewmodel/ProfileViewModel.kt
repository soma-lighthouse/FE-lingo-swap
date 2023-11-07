package com.lighthouse.profile.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.listener.InterestListener
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.request.UploadInterestVO
import com.lighthouse.domain.entity.response.vo.InterestVO
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
) : BaseViewModel(dispatcherProvider, application), InterestListener {
    val curProfile = ObservableField<ProfileVO>()

    private val _detail = MutableStateFlow<UiState>(UiState.Loading)
    val detail = _detail.asStateFlow()

    private val _create = MutableStateFlow<UiState>(UiState.Loading)
    val create = _create.asStateFlow()

    private val _error = MutableStateFlow<UiState>(UiState.Loading)
    val error = _error.asStateFlow()

    val languageList: ObservableList<String> = ObservableArrayList()
    val countryList: ObservableList<String> = ObservableArrayList()
    val saveObserver: ObservableBoolean = ObservableBoolean(false)

    val isEdit = ObservableBoolean(false)
    var startTime: Double = 0.0
    val isLoading = ObservableBoolean(true)
    var isMe = ObservableBoolean(false)
    var chat = ObservableBoolean(false)
    var opUid: String = ""

    var userProfile: ObservableField<RegisterInfoVO> = ObservableField()

    private var profileUrl: String? = null
    private var newFilePath: String? = null

    var editMode = false
    var imageUri: Uri? = null
    var filePath: String = ""


    fun getProfileDetail() {
        onIO {
            profileRepository.getProfileDetail(opUid.ifEmpty { getUUID() }).catch {
                _error.value = handleException(it)
            }.collect {
                curProfile.set(it)
                setRegisterInfo(it)
                languageList.addAll(it.languages.map { "${it.name}/LV${it.level}" })
                countryList.addAll(it.countries.map { it.name })
                _detail.value = UiState.Success(it.interests)

                homeRepository.saveLanguageVO(it.languages)
                homeRepository.saveCountryVO(it.countries)
                homeRepository.saveInterestVO(it.interests)
                isLoading.set(false)
            }
        }
    }

    private fun setRegisterInfo(data: ProfileVO) {
        val info = RegisterInfoVO(uuid = data.id,
            name = data.name,
            region = data.region.code,
            description = data.description,
            profileImageUri = data.profileImageUri,
            preferredCountries = data.countries.map { it.code },
            preferredInterests = data.interests.map {
                UploadInterestVO(it.category.code, it.interests.map { interest ->
                    interest.code
                })
            },
            languages = data.languages.map {
                mapOf("code" to it.code, "level" to it.level)
            })
        userProfile.set(info)
    }

    fun getDataFromLocal() {
        val info = userProfile.get()
        info?.let {
            languageList.clear()
            countryList.clear()
            val language = homeRepository.getLanguageVO()
            val country = homeRepository.getCountryVO()
            val interest = homeRepository.getInterestVO()
            val value = userProfile.get()
            value?.let {
                it.languages = language.map { l -> mapOf("code" to l.code, "level" to l.level) }
                it.preferredCountries = country.map { c -> c.code }
                it.preferredInterests = interest.map { i ->
                    UploadInterestVO(i.category.code, i.interests.map { interest ->
                        interest.code
                    })
                }
                userProfile.set(it)
            }
            languageList.addAll(language.map { "${it.name}/LV${it.level}" })
            countryList.addAll(country.map { it.name })
            _detail.value = UiState.Success(interest)
        }
    }


    fun getPreSignedUrl() {
        onIO {
            authRepository.getPreSignedURL(getFileName(filePath)).catch {
                _error.value = handleException(it)
            }.collect {
                profileUrl = it
                newFilePath = getFileName(filePath)
            }
        }
    }

    fun saveProfile() {
        isEdit.set(false)
        isLoading.set(true)
        if (!newFilePath.isNullOrEmpty()) {
            uploadImg(filePath)
        }
        saveUserDetail()
    }

    private fun saveUserDetail() {
        saveObserver.set(true)
        val endTime = System.currentTimeMillis().toDouble()
        newFilePath?.let {
            val change = userProfile.get()
            change?.let { r ->
                r.profileImageUri = it
            }
            userProfile.set(change)
        }

        onIO {
            try {
                if (userProfile.get()!!.profileImageUri == curProfile.get()!!.profileImageUri) {
                    val change = userProfile.get()
                    change?.let { r ->
                        r.profileImageUri = getFileName(curProfile.get()!!.profileImageUri)
                    }
                    userProfile.set(change)
                }
                updateProfileUseCase.invoke(
                    curProfile.get()!!,
                    userProfile.get()!!,
                    endTime - startTime
                )
            } catch (e: Exception) {
                _error.value = handleException(e)
            } finally {
                saveObserver.set(false)
                isLoading.set(false)
            }
        }
    }

    private fun getFileName(uri: String): String {
        if (uri == "") {
            return ""
        }
        val fileName = uri.substringAfterLast("/")
        return "/${getUUID()}/${fileName}"
    }

    private fun uploadImg(filePath: String) {
        onIO {
            authRepository.uploadImg(profileUrl!!, filePath).catch {
                _error.value = handleException(it)
            }.collect {
//                _register.value = UiState.Success(it)
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
            chatRepository.createChannel(opUid).catch {
                _detail.value = handleException(it)
            }.collect {
                _create.emit(UiState.Success(it))
            }
        }
    }

    fun getUUID() = profileRepository.getUUID()

    fun setNotification(b: Boolean) = profileRepository.setPushEnabled(b)

    fun getNotification() = profileRepository.getPushEnabled()

    override val selectedInterest: MutableLiveData<List<InterestVO>> =
        MutableLiveData<List<InterestVO>>()

    override val collapse: ObservableInt = ObservableInt(0)
}