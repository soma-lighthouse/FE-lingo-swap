package com.lighthouse.auth.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.ActivityLanguageBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageListActivity : BindingActivity<ActivityLanguageBinding>(R.layout.activity_language) {
    private lateinit var navController: NavController
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_language) as NavHostFragment
        navController = navHostFragment.navController

        viewModel.isRegister = intent.getBooleanExtra("isRegister", false)
        viewModel.checkLanguageUpdate()
    }
}