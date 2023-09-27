package com.lighthouse.android.common_ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.domain.entity.response.vo.LighthouseException
import java.net.UnknownHostException

open class BaseViewModel(
    dispatcherProvider: DispatcherProvider,
) : ViewModel(),
    DispatcherProvider by dispatcherProvider {

    protected fun handleException(e: Throwable): UiState.Error<*> {
        Log.e("BaseViewModel", e.stackTraceToString())
        return when (e) {
            is LighthouseException -> UiState.Error(e)
            is UnknownHostException -> UiState.Error(
                "Please check your internet connection and try again."
            )

            else -> UiState.Error(
                "Something went wrong. Please try again later."
            )
        }
    }
}