package com.lighthouse.auth.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.ActivityCountryBinding
import com.lighthouse.auth.databinding.LanguageTileBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.LanguageVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageListActivity : BindingActivity<ActivityCountryBinding>(R.layout.activity_country) {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var adapter: SimpleListAdapter<LanguageVO, LanguageTileBinding>
    private lateinit var selectedList: List<String>
    private var languageList = listOf<LanguageVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedList = intent.getStringArrayListExtra("selected")?.toList() ?: listOf()
        initBack()
        initAdapter()
        initSearch()
        getLanguageList()
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
                languageList = uiState.data as List<LanguageVO>
                adapter.submitList(languageList)
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
            finish()
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
                val filteredItems = languageList.filter { it.name.lowercase().contains(query) }
                adapter.submitList(filteredItems)
            }

        })
    }

    private fun initSelect(name: String, code: String) {
        if (name !in selectedList) {
            intent.putExtra("language", name)
            intent.putExtra("code", code)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            applicationContext.toast(getString(com.lighthouse.android.common_ui.R.string.duplicate_language))
        }
    }

    private fun initAdapter() {
        adapter = SimpleListAdapter(
            diffCallBack = ItemDiffCallBack(
                onItemsTheSame = { old, new -> old.name == new.name },
                onContentsTheSame = { old, new -> old == new }
            ),
            layoutId = R.layout.language_tile,
            onBindCallback = { viewHolder, item ->
                val binding = viewHolder.binding
                binding.tvCountry.text = item.name

                viewHolder.itemView.setOnClickListener {
                    initSelect(item.name, item.code)
                }
            }
        )

        val linearLayout = ScrollSpeedLinearLayoutManager(this, 8f)
        linearLayout.orientation = LinearLayoutManager.VERTICAL
        binding.rvCountry.layoutManager = linearLayout
        binding.rvCountry.adapter = adapter
    }


}