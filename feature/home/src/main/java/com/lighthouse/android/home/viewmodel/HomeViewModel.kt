package com.lighthouse.android.home.viewmodel

import android.app.Application
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.listener.InterestListener
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.android.home.util.getHomeTitle
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.domain.logging.MatchingTimeAndCountLogger
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.repository.ProfileRepository
import com.lighthouse.swm_logging.SWMLogging
import com.lighthouse.swm_logging.logging_scheme.ClickScheme
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
) : BaseViewModel(dispatcherProvider, application), InterestListener {
    private var userProfiles = listOf<ProfileVO>()
    var next: Int? = null

    private val _filter: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val filter: StateFlow<UiState> = _filter.asStateFlow()

    private val _changes = MutableLiveData<Boolean>()
    val changes: LiveData<Boolean> = _changes

    val countryNameList: ObservableList<String> = ObservableArrayList()

    val languageNameList: ObservableList<String> = ObservableArrayList()

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
                    homeRepository.saveLanguageVO(it.languages)
                    homeRepository.saveCountryVO(it.countries)
                    homeRepository.saveInterestVO(it.interests)
                    languageNameList.addAll(it.languages.map { "${it.name}/LV${it.level}" })
                    countryNameList.addAll(it.countries.map { it.name })
                    _filter.value = UiState.Success(it.interests)
                }
        }
    }

    fun uploadFilterSetting() {
        if (checkFilter()) {
            _changes.value = false
            return
        }
        onIO {
            homeRepository.uploadFilterSetting()
                .catch {
                    _filter.value = handleException(it)
                }
                .collect {
                    _changes.value = it
                    if (it) {
                        next = null
                        userProfiles = emptyList()
                    }
                }
        }
    }

    private fun checkFilter(): Boolean {
        return countryNameList.isEmpty() || languageNameList.isEmpty() || homeRepository.getInterestVO()
            .isEmpty()
    }

    fun getFilterFromLocal() {
        val language = homeRepository.getLanguageVO()
        val country = homeRepository.getCountryVO()
        languageNameList.clear()
        countryNameList.clear()
        languageNameList.addAll(language.map { "${it.name}/LV${it.level}" })
        countryNameList.addAll(country.map { it.name })
        _filter.value = UiState.Success(homeRepository.getInterestVO())
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

    fun setNotification(b: Boolean) = profileRepository.setPushEnabled(b)

    fun resetFilterState() {
        _filter.value = UiState.Loading
    }

    fun sendHomeClick(name: String, region: String, clickTime: Double, clickCount: Int) {
        val scheme = getHomeClickScheme(name, region, clickTime, clickCount)
        SWMLogging.logEvent(scheme)
    }

    private fun getHomeClickScheme(
        name: String,
        region: String,
        clickTime: Double,
        clickCount: Int
    ): ClickScheme {
        return MatchingTimeAndCountLogger.Builder()
            .setName(name)
            .setRegion(region)
            .setClickTime(clickTime)
            .setClickCount(clickCount)
            .build()
    }

    override val selectedInterest: MutableLiveData<List<InterestVO>> =
        MutableLiveData<List<InterestVO>>()

    override val collapse: ObservableInt = ObservableInt(0)
}