package com.lighthouse.auth.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.ActivityCountryBinding
import com.lighthouse.auth.databinding.LanguageTileBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.LanguageVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguagesFragment : BindingFragment<ActivityCountryBinding>(R.layout.activity_country) {
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<LanguageVO, LanguageTileBinding>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        initAdapter()
        initSearch()
        getLanguageList()
        observeResult()
    }


    private fun getLanguageList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getLanguageList()
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
                if (uiState.data is List<*>) {
                    adapter.submitList(uiState.data as List<LanguageVO>)
                }
                binding.pbCountry.setGone()
            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbCountry.setGone()
            }
        }
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
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
                    viewModel.language.filter { it.name.lowercase().contains(query) }
                adapter.submitList(filteredItems)
            }

        })
    }

    private fun initAdapter() {
        adapter = SimpleListAdapter(
            diffCallBack = ItemDiffCallBack(
                onItemsTheSame = { old, new -> old.code == new.code },
                onContentsTheSame = { old, new -> old == new }
            ),
            layoutId = R.layout.language_tile,
            onBindCallback = { viewHolder, item ->
                val binding = viewHolder.binding
                binding.item = item
                binding.viewModel = viewModel
            }
        )
        binding.rvCountry.adapter = adapter
    }

    private fun observeResult() {
        viewModel.changes.observe(viewLifecycleOwner) {
            if (it == -1) {
                findNavController().popBackStack()
            }
        }
    }
}