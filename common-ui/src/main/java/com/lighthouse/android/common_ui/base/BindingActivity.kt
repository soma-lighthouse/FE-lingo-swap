package com.lighthouse.android.common_ui.base

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.dialog.showOKDialog
import com.lighthouse.android.common_ui.util.Injector
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.domain.constriant.ErrorTypeHandling
import com.lighthouse.domain.entity.response.vo.LighthouseException
import com.lighthouse.navigation.MainNavigator
import dagger.hilt.android.EntryPointAccessors

abstract class BindingActivity<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int,
) : AppCompatActivity() {
    protected lateinit var binding: T

    protected val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                applicationContext.toast(result.data.toString())
            } else {
                applicationContext.toast("failed")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
    }

    val mainNavigator: MainNavigator by lazy {
        EntryPointAccessors.fromActivity(
            this,
            Injector.MainNavigatorInjector::class.java
        ).mainNavigator()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        EntryPointAccessors.fromActivity(
            this,
            Injector.SharedPreferencesInjector::class.java
        ).sharedPreferences()
    }

    protected fun getUUID(): String {
        return sharedPreferences.getString("com.lighthouse.lingo-talk.UUID", null) ?: ""
    }

    protected fun getUserName(): String {
        return sharedPreferences.getString("com.lighthouse.lingo-talk.USER_NAME", null) ?: ""
    }

    protected fun handleException(uiState: UiState.Error<*>) {
        val exception = uiState.message
        Log.d("ERROR", "enter handle exception")
        if (exception is LighthouseException) {
            when (exception.errorType) {
                ErrorTypeHandling.TOAST -> {
                    applicationContext.toast(exception.message.toString())
                }

                ErrorTypeHandling.DIALOG -> {
                    Log.d("ERROR", "enter dialog")
                    showOKDialog(
                        applicationContext,
                        getString(R.string.error_title),
                        exception.message.toString()
                    )
                }


                ErrorTypeHandling.DIRECT_AND_DIALOG -> {
                    showOKDialog(
                        applicationContext,
                        getString(R.string.error_title),
                        exception.message.toString(),
                        false,
                    ) { d, _ ->
                        d.dismiss()
                        logout()
                    }
                }

                ErrorTypeHandling.NONE -> {
                    // do nothing
                }
            }
        } else {
            applicationContext.toast(exception.toString())
        }
    }

    private fun logout() {
        sharedPreferences.edit().clear().apply()
        mainNavigator.navigateToLogin(applicationContext)
        finish()
    }


}