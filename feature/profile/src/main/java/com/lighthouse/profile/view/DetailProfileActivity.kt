package com.lighthouse.profile.view

import android.os.Bundle
import androidx.activity.viewModels
import com.lighthouse.android.common_ui.base.BindingActivity
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
    }
}