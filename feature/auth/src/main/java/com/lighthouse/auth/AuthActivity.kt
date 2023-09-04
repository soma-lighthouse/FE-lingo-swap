package com.lighthouse.auth

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.util.replace
import com.lighthouse.auth.databinding.ActivityAuthBinding
import com.lighthouse.auth.fragment.LoginFragment
import com.lighthouse.auth.viewmodel.AuthViewModel
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
//        viewModel.saveUUID()
        login()
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
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (::loginState.isInitialized) {
                        if (loginState == LoginState.LOGIN_SUCCESS) {
                            mainNavigator.navigateToMain(this@AuthActivity)
                            finish()
                        } else {
                            replace<LoginFragment>(R.id.nav_host_fragment_auth)
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