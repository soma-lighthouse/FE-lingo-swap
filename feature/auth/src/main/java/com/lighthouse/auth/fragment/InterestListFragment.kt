package com.lighthouse.auth.fragment

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
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentInterestBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.InterestVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InterestListFragment : BindingFragment<FragmentInterestBinding>(R.layout.fragment_interest) {
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>
    private val startTime = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        binding.text =
            if (viewModel.isRegister) getString(com.lighthouse.android.common_ui.R.string.next) else getString(
                com.lighthouse.android.common_ui.R.string.apply
            )
        initAdapter()
        getInterestList()
        initNext()
        initBack()
        viewModel.checkInterestUpdate()
    }

    private fun getInterestList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getInterestList()
                viewModel.result.collect {
                    render(it)
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = makeInterestAdapter(viewModel, true)
        binding.rvInterestList.adapter = adapter
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbInterest.setVisible()
                binding.rvInterestList.setGone()
                binding.btnApply.setGone()
            }

            is UiState.Success<*> -> {
                binding.rvInterestList.setVisible()
                binding.btnApply.setVisible()
                adapter.submitList(uiState.data as List<InterestVO>)
                binding.pbInterest.setGone()
            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbInterest.setGone()
            }
        }
    }

    private fun initNext() {
        viewModel.changes.observe(viewLifecycleOwner) {
            if (it == -3) {
                viewModel.sendRegisterClickLogging(
                    System.currentTimeMillis().toDouble() - startTime.toDouble(),
                    "interestScreen",
                    "interest_click"
                )
                if (viewModel.isRegister) {
                    findNavController().navigate(InterestListFragmentDirections.actionInterestFragmentToCountryFragment())
                } else {
                    requireActivity().finish()
                }
            }
        }
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
