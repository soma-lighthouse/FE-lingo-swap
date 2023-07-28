package com.lighthouse.android.home.view

import android.os.Bundle
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
import com.lighthouse.android.home.databinding.FragmentProfileBinding
import com.lighthouse.android.home.util.UiState
import com.lighthouse.android.home.util.setGone
import com.lighthouse.android.home.util.setVisible
import com.lighthouse.android.home.util.toast
import com.lighthouse.android.home.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: DrivenAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        initAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.drivenData.collect {
                    render(it)
                }
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbProfileLoading.setVisible()
                binding.rvProfile.setGone()
            }

            is UiState.Success -> {
                binding.rvProfile.setVisible()
                viewModel.pagingDataFlow
                adapter.submitList(uiState.drivenData)
                binding.pbProfileLoading.setGone()
            }

            is UiState.Error -> {
                context.toast(uiState.message)
                binding.pbProfileLoading.setGone()
            }

            else -> {
                binding.pbProfileLoading.setGone()
            }
        }
    }

    private fun initAdapter() {
        adapter = DrivenAdapter()
        binding.rvProfile.adapter = adapter

    }
}