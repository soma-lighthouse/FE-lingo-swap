package com.lighthouse.android.common_ui.base

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.entity.response.vo.LighthouseException
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    application: Application
) : AndroidViewModel(application),
    DispatcherProvider by dispatcherProvider {

    protected val context: Context
        get() = getApplication<Application>().applicationContext

    protected fun handleException(e: Throwable): UiState.Error<*> {
        Log.e("BaseViewModel", e.stackTraceToString())
        return when (e) {
            is LighthouseException -> UiState.Error(e)
            is UnknownHostException -> UiState.Error(
                context.getString(R.string.internet_error)
            )

            else -> UiState.Error(
                context.getString(R.string.server_error)
            )
        }
    }
}