package com.lighthouse.auth.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.auth.R
import com.lighthouse.auth.adapter.LanguageAdapter
import com.lighthouse.auth.databinding.ActivityCountryBinding
import com.lighthouse.domain.entity.response.vo.LanguageVO

class LanguageListActivity : BindingActivity<ActivityCountryBinding>(R.layout.activity_country) {
    private lateinit var adapter: LanguageAdapter
    private val languageList = mutableListOf(
        LanguageVO(name = "korean", level = 1),
        LanguageVO(name = "japanese", level = 1),
        LanguageVO(name = "chinese", level = 1)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBack()
        initAdapter()
        initSearch()

        adapter.submitList(languageList)

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
        adapter = LanguageAdapter {
            intent.putExtra("Language", it)
            setResult(RESULT_OK, intent)
            finish()
        }
        val linearLayout = ScrollSpeedLinearLayoutManager(this, 8f)
        linearLayout.orientation = LinearLayoutManager.VERTICAL
        binding.rvCountry.layoutManager = linearLayout
        binding.rvCountry.adapter = adapter
    }
}