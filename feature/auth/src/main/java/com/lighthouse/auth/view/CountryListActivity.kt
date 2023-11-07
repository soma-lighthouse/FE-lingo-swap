package com.lighthouse.auth.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.ActivityCountryBinding
import com.lighthouse.auth.selection_adapter.SelectionAdapter
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.CountryVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CountryListActivity : BindingActivity<ActivityCountryBinding>(R.layout.activity_country) {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var adapter: SelectionAdapter
    private var multiSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        multiSelection = intent.getBooleanExtra("multiSelect", false)
        binding.viewModel = viewModel
        binding.multi = multiSelection
        initBack()
        initAdapter()
        initSearch()
        getCountryList()
        observeClick()
    }

    private fun observeClick() {
        viewModel.changes.observe(this) {
            if (it == -1) {
                finish()
            } else {
                adapter.notifyItemChanged(it)
            }
        }
    }


    private fun getCountryList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getCountryList(multiSelection)
                viewModel.result.collect {
                    render(it)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbCountry.setVisible()
                binding.rvCountry.setGone()

            }

            is UiState.Success<*> -> {
                binding.rvCountry.setVisible()
                Log.d("TESTING DATA", uiState.data.toString())
                adapter.submitList(uiState.data as List<CountryVO>)
                viewModel.updateSelectedCountry()
                binding.pbCountry.setGone()
            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbCountry.setGone()
            }
        }
    }

    private fun initSearch() {
        binding.etCountry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                val filteredItems =
                    viewModel.country.filter { it.name.lowercase().contains(query) }
                adapter.submitList(filteredItems)
            }
        })
    }

    private fun initAdapter() {
        adapter = SelectionAdapter(
            multiSelection = multiSelection,
            viewModel = viewModel,
            context = applicationContext,
            type = SelectionAdapter.COUNTRY,
        )
        binding.rvCountry.adapter = adapter
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}