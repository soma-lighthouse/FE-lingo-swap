package com.lighthouse.android.common_ui.base

import androidx.lifecycle.ViewModel
import com.lighthouse.android.common_ui.util.DispatcherProvider

open class BaseViewModel(dispatcherProvider: DispatcherProvider) : ViewModel(),
    DispatcherProvider by dispatcherProvider