package com.lighthouse.android.home.viewmodel

import android.app.Application
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.listener.InterestListener
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.android.home.util.getHomeTitle
import com.lighthouse.domain.entity.request.UploadFilterVO
import com.lighthouse.domain.entity.response.FilterVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.MatchProfileVO
import com.lighthouse.domain.logging.FilterInteractLogger
import com.lighthouse.domain.logging.MatchingTimeAndCountLogger
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.ChatRepository
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.repository.ProfileRepository
import com.lighthouse.swm_logging.SWMLogging
import com.lighthouse.swm_logging.logging_scheme.ClickScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository,
    private val chatRepository: ChatRepository,
    private val dispatcherProvider: DispatcherProvider,
    application: Application
) : BaseViewModel(dispatcherProvider, application), InterestListener {
    private var userProfiles = listOf<MatchProfileVO>()
    private lateinit var newFilter: UploadFilterVO
    private lateinit var prevFilter: FilterVO
    var next: Int? = null

    private val _filter: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val filter: StateFlow<UiState> = _filter.asStateFlow()

    private val _chat: MutableStateFlow<String> = MutableStateFlow("")
    val chat: StateFlow<String> = _chat.asStateFlow()

    private val _changes = MutableSharedFlow<Boolean>()
    val changes: SharedFlow<Boolean> = _changes.asSharedFlow()

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
                    languageNameList.clear()
                    countryNameList.clear()
                    languageNameList.addAll(it.languages.map { "${it.name}/LV${it.level}" })
                    countryNameList.addAll(it.countries.map { it.name })
                    prevFilter = it
                    _filter.value = UiState.Success(it.interests)
                }
        }
    }

    fun uploadFilterSetting() {
        onIO {
            if (checkFilter()) {
                _changes.emit(false)
                return@onIO
            }
            newFilter = UploadFilterVO(
                homeRepository.getCountryVO().map { it.code },
                homeRepository.getLanguageVO()
                    .map { mapOf("code" to it.code, "level" to it.level) },
                homeRepository.getInterestVO().flatMap {
                    it.interests.map { i -> i.code }
                }
            )


            homeRepository.uploadFilterSetting(newFilter)
                .catch {
                    _filter.value = handleException(it)
                }
                .collect {
                    _changes.emit(it)
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


    fun saveUserProfiles(profiles: List<MatchProfileVO>) {
        userProfiles = profiles
    }

    fun getUserProfiles() = userProfiles

    fun setNotification(b: Boolean) = profileRepository.setPushEnabled(b)

    fun resetFilterState() {
        _filter.value = UiState.Loading
    }

    fun sendHomeClick(
        opUid: String,
        name: String,
        region: String,
        clickTime: Double,
        clickCount: Int
    ) {
        onIO {
            val scheme = getHomeClickScheme(opUid, name, region, clickTime, clickCount)
            SWMLogging.logEvent(scheme)
        }
    }

    private fun getHomeClickScheme(
        opUid: String,
        name: String,
        region: String,
        clickTime: Double,
        clickCount: Int
    ): ClickScheme {
        return MatchingTimeAndCountLogger.Builder()
            .setUuid(authRepository.getUserId() ?: "")
            .setName(name)
            .setRegion(region)
            .setClickTime(clickTime)
            .setClickCount(clickCount)
            .setOpUid(opUid)
            .build()
    }

    fun sendFilterClick(
        stayTime: Double,
    ) {
        onIO {
            val changedFilter = checkChanges()
            val scheme = getFilterClickScheme(stayTime, changedFilter)
            SWMLogging.logEvent(scheme)
        }
    }

    private fun checkChanges(): List<String> {
        val currentFilter = homeRepository.getInterestVO()
        val changedFilter = mutableListOf<String>()
        if (currentFilter.size != prevFilter.interests.size) {
            changedFilter.add("interest")
        } else {
            for (i in currentFilter.indices) {
                if (currentFilter[i].interests != prevFilter.interests[i].interests) {
                    changedFilter.add("interest")
                    break
                }
            }
        }
        if (countryNameList.size != prevFilter.countries.size) {
            changedFilter.add("country")
        } else {
            for (i in countryNameList.indices) {
                if (countryNameList[i] != prevFilter.countries[i].name) {
                    changedFilter.add("country")
                    break
                }
            }
        }
        if (languageNameList.size != prevFilter.languages.size) {
            changedFilter.add("language")
        } else {
            for (i in languageNameList.indices) {
                if (languageNameList[i] != "${prevFilter.languages[i].name}/LV${prevFilter.languages[i].level}") {
                    changedFilter.add("language")
                    break
                }
            }
        }
        return changedFilter
    }

    private fun getFilterClickScheme(
        stayTime: Double,
        changedFilter: List<String>,
    ): FilterInteractLogger {
        return FilterInteractLogger.Builder()
            .setUuid(authRepository.getUserId() ?: "")
            .setStayTime(stayTime)
            .setChangedFilter(changedFilter)
            .setFilter(newFilter)
            .build()
    }

    fun startChatting(userId: String) {
        onIO {
            chatRepository.createChannel(userId)
                .onStart {
                    _chat.value = ""
                }
                .catch {
                    _filter.value = handleException(it)
                }
                .collect {
                    _chat.value = it.id
                }
        }
    }

    fun clearChat() {
        _chat.value = ""
    }

    override val selectedInterest: MutableLiveData<List<InterestVO>> =
        MutableLiveData<List<InterestVO>>()

    override val collapse: ObservableInt = ObservableInt(0)
}