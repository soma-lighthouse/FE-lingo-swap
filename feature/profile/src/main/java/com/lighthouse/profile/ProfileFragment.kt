package com.lighthouse.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.android.common_ui.server_driven.viewholders.adapter.DrivenAdapter
import com.lighthouse.profile.databinding.FragmentProfileBinding
import com.lighthouse.profile.util.UiState
import com.lighthouse.profile.util.setGone
import com.lighthouse.profile.util.setVisible
import com.lighthouse.profile.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var adapter: DrivenAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        initAdapter()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.drivenData.collect {
                    render(it)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbProfileLoading.setVisible()
                binding.rvProfile.setGone()
            }

            is UiState.Success -> {
                binding.rvProfile.setVisible()
                adapter.submitList(uiState.drivenData)
                binding.pbProfileLoading.setGone()
            }

            is UiState.Error -> {
                context.toast(uiState.message)
                binding.pbProfileLoading.setGone()
            }
        }
    }

    private fun initAdapter() {
        adapter = DrivenAdapter()
        binding.rvProfile.adapter = adapter

    }
}