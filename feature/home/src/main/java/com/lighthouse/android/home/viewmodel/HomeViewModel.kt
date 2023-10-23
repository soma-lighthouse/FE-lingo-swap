package com.lighthouse.android.home.viewmodel

import android.app.Application
import android.text.SpannableStringBuilder
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.android.home.util.getHomeTitle
import com.lighthouse.domain.entity.request.UploadFilterVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository,
    private val dispatcherProvider: DispatcherProvider,
    application: Application
) : BaseViewModel(dispatcherProvider, application) {
    private var userProfiles = listOf<ProfileVO>()
    var next: Int? = null

    private val _filter: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val filter: StateFlow<UiState> = _filter.asStateFlow()

    private var _upload: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val upload: StateFlow<Boolean> = _upload.asStateFlow()

    var page = 1

    private lateinit var fetchJob: Job

    init {
        val key = homeRepository.getIfFilterUpdated()
        if (key) {
            userProfiles = listOf()
            homeRepository.saveIfFilterUpdated(false)
        }
    }

    fun fetchNextPage(
        pageSize: Int? = null,
    ) {
        if (::fetchJob.isInitialized) {
            fetchJob.cancel()
        }
        onIO {
            homeRepository.getMatchedUser(next, pageSize)
                .onStart {
                    _filter.value = UiState.Loading
                }
                .catch {
                    _filter.value = handleException(it)
                }.collect {
                    if (it.nextId == -1) {
                        page = -1
                    } else {
                        next = it.nextId
                    }
                    _filter.value = UiState.Success(it.profile)
                }
        }
    }

    fun getFilterFromServer() {
        onIO {
            homeRepository.getFilterSetting()
                .catch {
                    _filter.value = handleException(it)
                }
                .collect {
                    _filter.value = UiState.Success(it)
                }
        }
    }

    fun uploadFilterSetting(filter: UploadFilterVO) {
        onIO {
            homeRepository.uploadFilterSetting(filter)
                .catch {
                    _filter.value = handleException(it)
                }
                .collect {
                    _upload.value = it
                }
        }
    }

    suspend fun getSpannableText(): SpannableStringBuilder {
        return withContext(dispatcherProvider.io) {
            SpannableStringBuilderProvider.getSpannableBuilder(getHomeTitle(context), context)
        }
    }


    fun saveUserProfiles(profiles: List<ProfileVO>) {
        userProfiles = profiles
    }

    fun getUserProfiles() = userProfiles

    fun getLanguageFilter() = homeRepository.getLanguageFilter()

    fun saveLanguageFilter(languages: List<LanguageVO>) =
        homeRepository.saveLanguageFilter(languages)

    fun setNotification(b: Boolean) = profileRepository.setPushEnabled(b)

    fun resetUploadState() {
        _upload.value = false
    }

    fun resetFilterState() {
        _filter.value = UiState.Loading
    }

}