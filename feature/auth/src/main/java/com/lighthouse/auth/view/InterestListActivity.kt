package com.lighthouse.auth.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.ActivityInterestBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.InterestVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InterestListActivity : BindingActivity<ActivityInterestBinding>(R.layout.activity_interest) {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        initBack()
        initAdapter()
        getInterestList()
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
        viewModel.changes.observe(this) {
            if (it == -1) {
                finish()
            }
        }
    }

}

fun makeInterestAdapter(viewModel: AuthViewModel, highLight: Boolean) =
    SimpleListAdapter<InterestVO, InterestListTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onItemsTheSame = { old, new -> old.category == new.category },
            onContentsTheSame = { old, new -> old == new }),
        layoutId = com.lighthouse.android.common_ui.R.layout.interest_list_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding
            Log.d("TESTING INTEREST", item.toString())
            binding.item = item
            binding.clicked = viewModel.collapse
            binding.position = viewHolder.absoluteAdapterPosition
            binding.listener = viewModel
            binding.highLight = highLight
        }
    )
