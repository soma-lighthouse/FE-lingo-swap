package com.lighthouse.android.home.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.base.adapter.makeInterestAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.android.home.R
import com.lighthouse.android.home.databinding.FragmentFilterBinding
import com.lighthouse.android.home.viewmodel.HomeViewModel
import com.lighthouse.domain.entity.response.vo.InterestVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterFragment : BindingFragment<FragmentFilterBinding>(R.layout.fragment_filter) {
    private val viewModel: HomeViewModel by activityViewModels()
    private var first = true

    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initBack()
        initCountry()
        initLanguage()
        initInterest()
        initAdapter()
        getFilterFromServer()
        observeResult()
    }

    private fun observeResult() {
        viewModel.changes.observe(viewLifecycleOwner) {
            if (it) {
                context.toast(getString(com.lighthouse.android.common_ui.R.string.upload_success))
                findNavController().popBackStack()
            } else {
                context.toast(getString(com.lighthouse.android.common_ui.R.string.filter_error))
            }
        }
    }

    private fun initInterest() {
        binding.clickInterest.setOnClickListener {
            mainNavigator.navigateToInterest(
                requireContext(),
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (!first) {
            viewModel.getFilterFromLocal()
        }
    }


    private fun getFilterFromServer() {
        if (first) {
            viewModel.getFilterFromServer()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filter.collect {
                    render(it)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbFilter.setVisible()
                binding.filterGroup.setGone()
                binding.btnApply.setGone()
            }

            is UiState.Success<*> -> {
                if (uiState.data is List<*>) {
                    binding.filterGroup.setVisible()
                    val data = uiState.data as List<InterestVO>
                    adapter.submitList(data)
                    first = false
                }
                binding.pbFilter.setGone()
                binding.btnApply.setVisible()
            }

            is UiState.Error<*> -> {
                handleException(uiState)
            }
        }
    }


    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initCountry() {
        binding.clickCountry.setOnClickListener {
            mainNavigator.navigateToCountry(
                requireContext(),
                Pair("multiSelect", true),
            )
        }
    }

    private fun initLanguage() {
        binding.clickLanguage.setOnClickListener {
            mainNavigator.navigateToLanguage(
                requireContext(),
                Pair("isRegister", false),
            )
        }
    }

    private fun initAdapter() {
        adapter = makeInterestAdapter(viewModel, false)
        binding.rvInterest.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycleScope.cancel()
    }
}