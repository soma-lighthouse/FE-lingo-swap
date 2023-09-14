package com.lighthouse.android.common_ui.base

import androidx.lifecycle.ViewModel
import com.lighthouse.domain.constriant.ErrorTypeHandling

abstract class BaseViewModel : ViewModel() {
    protected fun ErrorResult(msg: String, errorType: ErrorTypeHandling) {
        
    }
}