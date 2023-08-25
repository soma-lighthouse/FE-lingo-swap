package com.lighthouse.auth.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.lighthouse.auth.adapter.CountryAdapter
import com.lighthouse.auth.databinding.ActivityCountryBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.CountryVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable

@AndroidEntryPoint
class CountryListActivity : BindingActivity<ActivityCountryBinding>(R.layout.activity_country),
    CountryAdapter.OnItemClickListener {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var adapter: CountryAdapter
    private var resultList = MutableLiveData<List<CountryVO>>()
    private var countryList = listOf<CountryVO>()
    private var multiSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        multiSelection = intent.getBooleanExtra("multiSelect", false)


        initBack()
        initAdapter()
        initApply()
        initSearch()
        getCountryList()

        adapter.submitList(countryList)
        if (multiSelection) {
            resultList.observe(this) {
                initChip()

            }
        } else {
            binding.chipResult.setGone()
        }
    }

    private fun getCountryList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getCountryList().collect {
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
                countryList = uiState.data as List<CountryVO>
                adapter.submitList(countryList)
                binding.pbCountry.setGone()
            }

            is UiState.Error -> {
                applicationContext.toast(uiState.message)
                binding.pbCountry.setGone()
            }
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
                for (i in countryList.indices) {
                    if (countryList[i].name == it.name) {
                        adapter.currentList[i].select = false
                        adapter.notifyItemChanged(i)
                        break
                    }
                }
            }

            binding.chipResult.addView(chip)
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
                val filteredItems = countryList.filter { it.name.lowercase().contains(query) }
                adapter.submitList(filteredItems)
            }

        })
    }

    private fun initAdapter() {
        adapter = CountryAdapter(this, multiSelection)
        val linearLayout = ScrollSpeedLinearLayoutManager(this, 8f)
        linearLayout.orientation = LinearLayoutManager.VERTICAL
        binding.rvCountry.layoutManager = linearLayout
        binding.rvCountry.adapter = adapter
    }

    private fun initApply() {
        binding.btnApply.setOnClickListener {
            intent.putExtra("CountryList", resultList.value as Serializable)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(item: CountryVO) {
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