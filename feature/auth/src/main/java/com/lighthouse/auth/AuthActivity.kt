package com.lighthouse.auth

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.auth.databinding.ActivityAuthBinding
import com.lighthouse.auth.viewmodel.AuthViewModel

class AuthActivity : BindingActivity<ActivityAuthBinding>(R.layout.activity_auth) {
    private val viewModel: AuthViewModel by viewModels()
    lateinit var loginState: LoginState

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        checkRegister()

    }

    private fun checkRegister() {
        loginState = if (viewModel.getUUID() != "") {
            LoginState.LOGIN_SUCCESS
        } else {
            LoginState.LOGIN_FAILURE
        }
    }

    private fun login() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            ViewTreeObserver.OnPreDrawListener {
                if (::loginState.isInitialized) {
                    if (loginState == LoginState.LOGIN_SUCCESS) {
                        mainNavigator.navigateToMain(this@AuthActivity)
                        finish()
                    } else {
                    }
                }
                true
            }
        )
    }
}