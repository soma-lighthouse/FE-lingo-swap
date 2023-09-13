package com.lighthouse.auth

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.dialog.showOKDialog
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.auth.databinding.ActivityAuthBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BindingActivity<ActivityAuthBinding>(R.layout.activity_auth) {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var loginState: LoginState
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment
        navController = navHostFragment.navController

        checkRegister()
        login()
        handleBackPressed()
    }

    private fun handleBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showOKDialog(
                    this@AuthActivity,
                    getString(com.lighthouse.android.common_ui.R.string.exit_title),
                    getString(com.lighthouse.android.common_ui.R.string.exit_body)
                ) { _, _ ->
                    if (navController.currentDestination?.id == R.id.loginFragment) {
                        finish()
                    } else if (navController.currentDestination?.id == R.id.info_fragment) {
                        navController.popBackStack()
                    }
                }
            }
        }

        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun checkRegister() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                loginState = if (viewModel.getAccessToken() == "") {
                    viewModel.postGoogleLogin()
                    viewModel.result
                        .drop(1)
                        .filterIsInstance<UiState>()
                        .map { loginState ->
                            when (loginState) {
                                is UiState.Success<*> -> LoginState.LOGIN_SUCCESS
                                is UiState.Error<*> -> LoginState.LOGIN_FAILURE
                                else -> LoginState.LOGIN_FAILURE
                            }
                        }
                        .first()
                } else {
                    LoginState.LOGIN_FAILURE
                }
            }
        }
    }


    private fun login() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (::loginState.isInitialized) {
                        if (loginState == LoginState.LOGIN_SUCCESS) {
                            mainNavigator.navigateToMain(this@AuthActivity)
                            finish()
                        }
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            })
    }
}