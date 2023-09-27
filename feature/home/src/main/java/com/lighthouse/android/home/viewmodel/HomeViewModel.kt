package com.lighthouse.android.home.viewmodel

import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.domain.constriant.Resource
import com.lighthouse.domain.entity.request.UploadFilterVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.usecase.GetFilterSettingUseCase
import com.lighthouse.domain.usecase.GetLanguageFilterUseCase
import com.lighthouse.domain.usecase.GetMatchedUserUseCase
import com.lighthouse.domain.usecase.ManageFilterUpdateUseCase
import com.lighthouse.domain.usecase.SaveLanguageFilterUseCase
import com.lighthouse.domain.usecase.UploadFilterSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMatchedUserUseCase: GetMatchedUserUseCase,
    private val getLanguageFilterUseCase: GetLanguageFilterUseCase,
    private val getFilterSettingUseCase: GetFilterSettingUseCase,
    private val saveLanguageFilterUseCase: SaveLanguageFilterUseCase,
    private val uploadFilterSettingUseCase: UploadFilterSettingUseCase,
    manageFilterUpdateUseCase: ManageFilterUpdateUseCase,
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel(dispatcherProvider) {
    private var userProfiles = listOf<ProfileVO>()
    var next: Int? = null

    private val _filter: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val filter: StateFlow<UiState> = _filter.asStateFlow()

    private var _upload: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val upload: StateFlow<Boolean> = _upload.asStateFlow()

    var page = 1

    private lateinit var fetchJob: Job

    init {
        val key = manageFilterUpdateUseCase.getIfFilterUpdated()
        if (key) {
            userProfiles = listOf()
            manageFilterUpdateUseCase.saveIfFilterUpdated(false)
        }
    }

    fun fetchNextPage(
        pageSize: Int? = null,
    ) {
        if (::fetchJob.isInitialized) {
            fetchJob.cancel()
        }
        onIO {
            getMatchedUserUseCase.invoke(next, pageSize)
                .onStart {
                    _filter.value = UiState.Loading
                }
                .catch {
                    _filter.value = handleException(it)
                }.collect {
                    when (it) {
                        is Resource.Success -> {
                            if (it.data!!.nextId == -1) {
                                page = -1
                            } else {
                                next = it.data!!.nextId
                            }
                            _filter.value = UiState.Success(it.data!!.profile)
                        }

                        is Resource.Error ->
                            _filter.value = UiState.Error(it.message ?: StringSet.error_msg)
                    }
                }
        }
    }

    fun getFilterFromServer() {
        onIO {
            getFilterSettingUseCase.invoke()
                .catch {
                    _filter.value = handleException(it)
                }
                .collect {
                    when (it) {
                        is Resource.Success -> {
                            _filter.value = UiState.Success(it.data!!)
                        }

                        is Resource.Error -> {
                            _filter.value = UiState.Error(it.message ?: StringSet.error_msg)
                        }
                    }
                }
        }
    }

    fun uploadFilterSetting(filter: UploadFilterVO) {
        onIO {
            uploadFilterSettingUseCase.invoke(filter)
                .catch {
                    _filter.value = handleException(it)
                }
                .collect {
                    when (it) {
                        is Resource.Success -> {
                            _upload.value = it.data!!
                        }

                        is Resource.Error -> {
                            _filter.value = UiState.Error(it.message ?: StringSet.error_msg)
                        }
                    }
                }
        }
    }

    fun saveUserProfiles(profiles: List<ProfileVO>) {
        userProfiles = profiles
    }

    fun getUserProfiles() = userProfiles

    fun getLanguageFilter() = getLanguageFilterUseCase.invoke()

    fun saveLanguageFilter(languages: List<LanguageVO>) =
        saveLanguageFilterUseCase.invoke(languages)

    fun resetUploadState() {
        _upload.value = false
    }

    fun resetFilterState() {
        _filter.value = UiState.Loading
    }

}