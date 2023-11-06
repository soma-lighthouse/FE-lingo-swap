package com.lighthouse.profile.view

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.dialog.showOKDialog
import com.lighthouse.profile.R
import com.lighthouse.profile.databinding.ActivityDetailProfileBinding
import com.lighthouse.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailProfileActivity :
    BindingActivity<ActivityDetailProfileBinding>(R.layout.activity_detail_profile) {
    private val viewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.isMe.set(intent.getBooleanExtra("isMe", false))
        viewModel.chat.set(intent.getBooleanExtra("isChat", false))

        handleBackPressed()
    }

    private fun handleBackPressed() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.editMode) {
                    showOKDialog(
                        this@DetailProfileActivity,
                        getString(com.lighthouse.android.common_ui.R.string.exit_title),
                        getString(com.lighthouse.android.common_ui.R.string.exit_body)
                    ) { _, _ ->
                        finish()
                    }
                } else {
                    if (navController.currentDestination?.id == R.id.detailFragment) {
                        finish()
                    } else {
                        navController.popBackStack()
                    }
                }
            }
        }

        this.onBackPressedDispatcher.addCallback(this, callback)
    }
}