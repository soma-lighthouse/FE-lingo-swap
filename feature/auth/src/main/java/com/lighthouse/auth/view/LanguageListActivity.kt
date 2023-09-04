package com.lighthouse.auth.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.auth.R
import com.lighthouse.auth.adapter.SelectionAdapter
import com.lighthouse.auth.databinding.ActivityCountryBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.Selection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageListActivity : BindingActivity<ActivityCountryBinding>(R.layout.activity_country),
    SelectionAdapter.OnItemClickListener {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var adapter: SelectionAdapter
    private lateinit var selectedList: List<String>
    private var languageList = listOf<LanguageVO>()
    private var resultList = MutableLiveData<List<LanguageVO>>()
    private var multiSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedList = intent.getStringArrayListExtra("selected")?.toList() ?: listOf()
        multiSelection = intent.getBooleanExtra("multiSelect", false)
        initBack()
        initAdapter()
        initSearch()
        getLanguageList()
        initChip()
        initApply()
        observeResult()
    }

    private fun initApply() {
        binding.btnApply.setOnClickListener {
            if (resultList.value != null) {
                intent.putStringArrayListExtra(
                    "LanguageNameList",
                    ArrayList(resultList.value!!.map { it.name })
                )
                intent.putStringArrayListExtra(
                    "LanguageCodeList",
                    ArrayList(resultList.value!!.map { it.code })
                )
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initSelect() {
        val result = mutableListOf<LanguageVO>()
        for (n in selectedList) {
            val select = languageList.find { it.name == n }
            if (select != null) {
                val index = languageList.indexOf(select)
                select.select = true
                result.add(select)
                adapter.selectCnt += 1
                adapter.notifyItemChanged(index)
            }
        }

        resultList.value = result
    }

    private fun observeResult() {
        if (multiSelection) {
            resultList.observe(this) {
                initChip()
            }
        } else {
            binding.chipResult.setGone()
        }
    }

    private fun initChip() {
        binding.chipResult.removeAllViews()
        val inflater = LayoutInflater.from(applicationContext)
        resultList.value?.forEach {
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.home_chip, binding.chipResult, false
            ) as Chip

            chip.text = it.name
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener { c ->
                binding.chipResult.removeView(c)
                for (i in languageList.indices) {
                    if (languageList[i].name == it.name) {
                        resultList.value = resultList.value!!.minus(languageList[i])
                        adapter.currentList[i].select = false
                        adapter.notifyItemChanged(i)
                        adapter.selectCnt -= 1
                        break
                    }
                }
            }

            binding.chipResult.addView(chip)
        }
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
                initSelect()
                binding.pbCountry.setGone()
            }

            is UiState.Error -> {
                applicationContext.toast(uiState.message)
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

    private fun initAdapter() {
        adapter = SelectionAdapter(
            multiSelection = multiSelection,
            listener = this,
            context = applicationContext,
            type = SelectionAdapter.LANGUAGE
        )

        val linearLayout = ScrollSpeedLinearLayoutManager(this, 8f)
        linearLayout.orientation = LinearLayoutManager.VERTICAL
        binding.rvCountry.layoutManager = linearLayout
        binding.rvCountry.adapter = adapter
    }

    override fun onItemClick(item: Selection) {
        item as LanguageVO
        Log.d("LIMIT", item.toString())
        if (resultList.value != null && resultList.value!!.contains(item)) {
            resultList.value = resultList.value!!.minus(item)
        } else {
            if (multiSelection && resultList.value != null) {
                resultList.value = resultList.value!!.plus(item)
            } else {
                resultList.value = mutableListOf(item)
            }
        }
    }
}