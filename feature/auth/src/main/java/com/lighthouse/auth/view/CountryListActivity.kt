package com.lighthouse.auth.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.auth.R
import com.lighthouse.auth.adapter.CountryAdapter
import com.lighthouse.auth.databinding.ActivityCountryBinding
import com.lighthouse.domain.entity.response.vo.CountryVO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryListActivity : BindingActivity<ActivityCountryBinding>(R.layout.activity_country),
    CountryAdapter.OnItemClickListener {
    private lateinit var adapter: CountryAdapter
    private var resultList = MutableLiveData<List<String>>()
    private val countryList = mutableListOf(
        CountryVO(name = "Korea", code = "kr"),
        CountryVO(name = "USA", code = "us"),
        CountryVO(name = "England", code = "uk"),
        CountryVO(name = "Japan", code = "jp"),
        CountryVO(name = "China", code = "cn"),
    )
    private val multiSelection = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBack()
        initAdapter()
        initApply()
        initSearch()

        resultList.observe(this) {
            initChip()
            Log.d("CHECKING", it.toString())
        }
        adapter.submitList(countryList)
    }

    private fun initChip() {
        binding.chipResult.removeAllViews()
        val inflater = LayoutInflater.from(applicationContext)
        resultList.value?.forEach {
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.home_chip, binding.chipResult, false
            ) as Chip

            chip.text = it
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener { c ->
                binding.chipResult.removeView(c)
                for (i in countryList.indices) {
                    if (countryList[i].name == it) {
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
        // TODO()
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(item: String) {
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