package com.lighthouse.android.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.android.common_ui.server_driven.adapter.DrivenAdapter
import com.lighthouse.android.home.R
import com.lighthouse.android.home.databinding.FragmentHomeBinding
import com.lighthouse.android.home.util.UiState
import com.lighthouse.android.home.util.setGone
import com.lighthouse.android.home.util.setVisible
import com.lighthouse.android.home.util.toast
import com.lighthouse.android.home.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: DrivenAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        initAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeData.collect { uistate ->
                    render(uistate)
                }
            }

        }
        return binding.root
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbHomeLoading.setVisible()
                binding.rvHome.setGone()
            }

            is UiState.Success -> {
                Log.d("RESPONSE", uiState.drivenData.toString())
                binding.rvHome.setVisible()
                adapter.submitList(uiState.drivenData)
                binding.pbHomeLoading.setGone()
            }

            is UiState.Error -> {
                context.toast(uiState.message)
                binding.pbHomeLoading.setGone()
            }
        }
    }

    private fun initAdapter() {
        adapter = DrivenAdapter()
        binding.rvHome.adapter = adapter
    }
}