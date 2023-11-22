package com.lighthouse.auth.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.base.adapter.makeInterestAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.ActivityInterestListBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.InterestVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InterestListActivity :
    BindingActivity<ActivityInterestListBinding>(R.layout.activity_interest_list) {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        binding.text =
            if (viewModel.isRegister) getString(com.lighthouse.android.common_ui.R.string.next) else getString(
                com.lighthouse.android.common_ui.R.string.apply
            )
        initAdapter()
        getInterestList()
        initBack()
        initApply()
    }

    override fun onStart() {
        super.onStart()
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

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initApply() {
        viewModel.changes.observe(this) {
            if (it == -3) {
                finish()
            }
        }
    }
}