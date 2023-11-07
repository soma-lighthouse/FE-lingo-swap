package com.lighthouse.auth.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.BaseViewModel
import com.lighthouse.android.common_ui.listener.InterestListener
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.isValidBirthday
import com.lighthouse.android.common_ui.util.isValidEmail
import com.lighthouse.android.common_ui.util.isValidName
import com.lighthouse.android.common_ui.util.onIO
import com.lighthouse.auth.BuildConfig
import com.lighthouse.domain.constriant.LoginState
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.request.UploadInterestVO
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.logging.RegisterExposureLogger
import com.lighthouse.domain.repository.AuthRepository
import com.lighthouse.domain.repository.HomeRepository
import com.lighthouse.domain.usecase.CheckLoginStatusUseCase
import com.lighthouse.swm_logging.SWMLogging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    private val loginStatus: CheckLoginStatusUseCase,
    dispatcherProvider: DispatcherProvider,
    application: Application
) : BaseViewModel(dispatcherProvider, application), InterestListener {
    private val _loginState: MutableLiveData<LoginState> = MutableLiveData()
    val loginState: LiveData<LoginState> = _loginState

    private val _error = MutableLiveData<Map<String, String>>()
    val error: LiveData<Map<String, String>> = _error

    override val collapse = ObservableInt(0)

    val selectedCountry = MutableLiveData<List<CountryVO>>()
    override val selectedInterest = MutableLiveData<List<InterestVO>>()
    val selectedLanguage = MutableLiveData(listOf(LanguageVO("English", 1, "en")))

    val loading = ObservableBoolean(false)

    private var _country = listOf<CountryVO>()
    val country
        get() = _country

    private var _language = listOf<LanguageVO>()
    val language
        get() = _language

    private var _interest = listOf<InterestVO>()

    private val _changes = MutableLiveData<Int>()
    val changes: LiveData<Int> = _changes

    private val _remove = MutableLiveData<Int>()
    val remove: LiveData<Int> = _remove

    private var _selectedPosition: Int = 0

    val userId: UUID = UUID.randomUUID()
    val registerInfo = RegisterInfoVO()
    var profilePath: String? = null
    var profileUrl: String? = null
    var filePath: String = ""
    val errorNumber: MutableLiveData<List<Int>> = MutableLiveData()
    val collect: ObservableBoolean = ObservableBoolean()

    private val _result = MutableStateFlow<UiState>(UiState.Loading)
    val result: StateFlow<UiState> = _result.asStateFlow()

    private val _upload = MutableStateFlow(false)
    val upload: StateFlow<Boolean> = _upload.asStateFlow()

    private val _register = MutableStateFlow(false)
    val register: StateFlow<Boolean> = _register.asStateFlow()

    val languageLevel = arrayListOf(
        context.resources.getString(R.string.level1),
        context.resources.getString(R.string.level2),
        context.resources.getString(R.string.level3),
        context.resources.getString(R.string.level4),
        context.resources.getString(R.string.level5)
    )

    init {
        checkUpdate()
    }

    fun clearAllData() {
        homeRepository.clearAllData()
    }

    private fun checkUpdate() {
        viewModelScope.launch {
            val minVersion = homeRepository.fetchRemoteConfig("MIN_VER").split(".")
            val currentVersion = BuildConfig.CURRENT_VER.split(".")
            Log.d("TESTING VERSION", minVersion.toString())
            if (minVersion.size != currentVersion.size) {
                _error.value = mapOf()
                return@launch
            }
            for (i in minVersion.indices) {
                if (minVersion[i].toInt() > currentVersion[i].toInt()) {
                    _error.value = mapOf("error" to "true", "type" to "dialog")
                    return@launch
                }
            }
            _error.value = mapOf()
        }
    }

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
            authRepository.getInterestList().catch {
                _result.value = handleException(it)
            }.collect {
                _interest = it
                _result.emit(UiState.Success(it))
            }
        }
    }


    fun getLanguageList() {
        onIO {
            authRepository.getLanguageList().catch {
                _result.value = handleException(it)
            }.collect {
                _language = it
                _result.emit(UiState.Success(it))
            }
        }
    }


    fun getCountryList(multi: Boolean) {
        onIO {
            authRepository.getCountryList().catch {
                _result.value = handleException(it)
            }.collect {
                _country = it
                _result.emit(UiState.Success(it))
            }
        }
        checkCountryUpdate(multi)
    }

    fun registerUser() {
        onIO {
            authRepository.registerUser(registerInfo)
                .catch {
                    _register.value = false
                }.collect {
                    loading.set(false)
                    _register.value = true
                }
        }
    }

    fun registerBasicInfo() {
        collect.set(true)
        registerInfo.region = selectedCountry.value?.first()?.code
        registerInfo.preferredInterests = selectedInterest.value?.map {
            UploadInterestVO(it.category.code, it.interests.map { c -> c.code })
        }
        validateInputs()
        val intersect =
            errorNumber.value?.intersect(setOf(1, 2, 3, 4, 5, 6)) ?: listOf()
        if (intersect.isEmpty()) {
            _changes.value = next
            _changes.value = none
            return
        }
    }

    fun getPreSignedUrl(fileName: String) {
        onIO {
            authRepository.getPreSignedURL(fileName).catch {
                _result.value = handleException(it)
            }.collect {
                _result.emit(UiState.Success(it))
            }
        }
    }

    fun uploadImg(filePath: String) {
        onIO {
            authRepository.uploadImg(profileUrl!!, filePath).catch {
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
            authRepository.postGoogleLogin().onStart {
                _result.value = UiState.Loading
            }.catch {
                _result.value = handleException(it)
            }.collect {
                _result.emit(UiState.Success(it))
            }
        }
    }

    fun clearResult() {
        _result.value = UiState.Loading
    }

    private fun validateInputs() {
        val tmp = mutableListOf<Int>().apply {
            if (!registerInfo.name.isValidName()) {
                add(1)
            }
            if (!registerInfo.birthday.isValidBirthday()) {
                add(2)
            }
            if (!registerInfo.email.isValidEmail()) {
                add(3)
            }
            if (registerInfo.gender == "TMP") {
                add(4)
            }
            if (registerInfo.region.isNullOrEmpty()) {
                add(5)
            }
            if (selectedInterest.value.isNullOrEmpty()) {
                add(6)
            }
            if (selectedCountry.value.isNullOrEmpty()) {
                add(7)
            }
            if (registerInfo.description.isNullOrEmpty()) {
                add(8)
            }
        }
        collect.set(false)
        errorNumber.value = tmp
        Log.d("TESTING ERROR", errorNumber.value.toString())
        if (tmp.isEmpty()) {
            errorNumber.value = listOf(0)
        }
    }

    fun getClickPosition(position: Int, multi: Boolean) {
        val data = selectedCountry.value ?: listOf()

        if (!multi) {
            _country.forEachIndexed { index, it ->
                if (it.select) {
                    it.select = false
                    _changes.value = index
                }
            }
            selectedCountry.value = listOf(_country[position])
        } else if (data.size == Constant.MAX_SELECTION && _country[position] !in data) {
            return
        } else if (_country[position] !in data) {
            selectedCountry.value = data + _country[position]
        } else {
            selectedCountry.value = data - _country[position]
        }
        _country[position].select = !_country[position].select
        _changes.value = position
    }

    fun chipCloseListener(c: CountryVO) {
        selectedCountry.value = selectedCountry.value!! - c
        val index = _country.indexOf(c)
        if (index != -1) {
            _country[index].select = false
            _changes.value = index
        }
    }

    fun saveCountry(multi: Boolean) {
        if (multi) {
            homeRepository.saveCountryVO(selectedCountry.value ?: listOf())
            registerInfo.preferredCountries = selectedCountry.value?.map { it.code }

        } else {
            homeRepository.saveRegion(selectedCountry.value?.first() ?: CountryVO("", ""))
            registerInfo.region = selectedCountry.value?.first()?.code
        }
        _changes.value = finish
    }

    fun checkCountryUpdate(multi: Boolean) {
        if (multi) {
            selectedCountry.value = homeRepository.getCountryVO()
        } else {
            selectedCountry.value = listOf(homeRepository.getRegion())
        }
    }

    fun updateSelectedCountry() {
        selectedCountry.value!!.forEach {
            val index = _country.indexOf(it)
            if (index != -1) {
                _country[index].select = true
                _changes.value = index
            }
        }
    }


    fun startRegister() {
        validateInputs()
        if (errorNumber.value!!.size == 1 && errorNumber.value!!.first() == 0) {
            registerInfo.uuid = userId.toString()
            registerInfo.preferredCountries = selectedCountry.value?.map { it.code }
            loading.set(true)
            if (filePath != "") {
                uploadImg(filePath)
            } else {
                registerUser()
            }
        }
    }

    fun uploadInterest() {
        var interest = selectedInterest.value
        interest?.let {
            interest = it.filter {
                it.interests.isNotEmpty()
            }
            homeRepository.saveInterestVO(it)
            _changes.value = finish

        }
    }

    fun checkInterestUpdate() {
        selectedInterest.value = homeRepository.getInterestVO()
    }

    fun checkLanguageUpdate() {
        val list = homeRepository.getLanguageVO()
        if (list.isNotEmpty()) {
            selectedLanguage.value = list
        }
    }

    fun onLanguageClick(item: LanguageVO) {
        val languages = selectedLanguage.value?.toMutableList()
        languages?.let {
            languages[_selectedPosition].name = item.name
            languages[_selectedPosition].code = item.code
            selectedLanguage.value = languages
        }
        _changes.value = finish
        _changes.value = none
    }

    fun removeLanguage(item: LanguageVO, position: Int) {
        val selected = selectedLanguage.value?.toMutableList()
        selected?.let {
            selected.remove(item)
            selectedLanguage.value = selected
        }
        _remove.value = position
    }

    fun selectLanguage(position: Int) {
        _selectedPosition = position
        _changes.value = direct
    }

    fun addLanguage() {
        if (selectedLanguage.value?.size == 5) {
            return
        }
        selectedLanguage.value =
            selectedLanguage.value?.plus(LanguageVO(context.getString(R.string.language), 1, ""))
    }

    fun updateLanguage(isRegister: Boolean) {
        val list = selectedLanguage.value
        list?.let {
            val final = it.filter { l -> l.name != context.getString(R.string.language) }
            homeRepository.saveLanguageVO(final)
            registerInfo.languages = final.map { l -> mapOf("code" to l.code, "level" to l.level) }
            _changes.value = if (isRegister) next else finish
            _changes.value = none
        }
    }

    fun sendRegisterExposureLogging(stayTime: Double) {
        val scheme = getRegisterExposureLogging(stayTime)
        SWMLogging.logEvent(scheme)
    }

    private fun getRegisterExposureLogging(stayTime: Double): RegisterExposureLogger {
        return RegisterExposureLogger.Builder()
            .setStayTime(stayTime)
            .build()
    }

    companion object {
        const val finish = -1
        const val next = -3
        const val direct = -4
        const val none = -99
    }
}