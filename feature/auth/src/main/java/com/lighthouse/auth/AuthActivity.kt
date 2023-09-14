package com.lighthouse.auth

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.dialog.showOKDialog
import com.lighthouse.auth.databinding.ActivityAuthBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.constriant.LoginState
import dagger.hilt.android.AndroidEntryPoint

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
        viewModel.getLoginStatus()
        viewModel.loginState.observe(this) {
            loginState = it
        }
    }


    private fun login() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
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