package com.lighthouse.android.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.android.common_ui.server_driven.adapter.DrivenAdapter
import com.lighthouse.android.home.databinding.ActivityMainBinding
import com.lighthouse.android.home.util.UiState
import com.lighthouse.android.home.util.setGone
import com.lighthouse.android.home.util.setVisible
import com.lighthouse.android.home.util.toast
import com.lighthouse.android.home.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DrivenAdapter
    val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        initAdapter()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataDriven.collect { uistate ->
                    render(uistate)
                }
            }
        }

    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbLoading.setVisible()
                binding.rvDriven.setGone()
            }

            is UiState.Success -> {
                binding.rvDriven.setVisible()
                adapter.submitList(uiState.drivenData)
                binding.pbLoading.setGone()
            }

            is UiState.Error -> {
                toast(uiState.message)
                binding.pbLoading.setGone()
            }
        }
    }

    private fun initAdapter() {
        adapter = DrivenAdapter()
        binding.rvDriven.adapter = adapter
    }
}