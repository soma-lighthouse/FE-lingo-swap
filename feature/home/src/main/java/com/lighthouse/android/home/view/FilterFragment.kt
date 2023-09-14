package com.lighthouse.android.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.base.adapter.makeAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.util.intentSerializable
import com.lighthouse.android.home.R
import com.lighthouse.android.home.databinding.FragmentFilterBinding
import com.lighthouse.domain.entity.response.vo.InterestVO

class FilterFragment : BindingFragment<FragmentFilterBinding>(R.layout.fragment_filter) {
    private var selectedCountryName = mutableListOf<String>()
    private var selectedCountryCode = mutableListOf<String>()
    private var selectLanguageName = mutableListOf<String>()
    private var selectLanguageCode = mutableListOf<String>()
    private var interestList = mutableListOf<InterestVO>()

    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        observeCountryResult()
        observerInterestResult()
        observeLanguageResult()
        initCountry()
        initLanguage()
        initInterest()
        initAdapter()
    }

    private fun initCountry() {
        binding.clickCountry.setOnClickListener {
            val intent =
                mainNavigator.navigateToCountry(
                    requireContext(),
                    Pair("multiSelect", true),
                    Pair("SelectedList", selectedCountryName)
                )
            resultLauncher.launch(intent)
        }
    }

    private fun initLanguage() {
        binding.clickLanguage.setOnClickListener {
            val intent = mainNavigator.navigateToLanguage(
                requireContext(),
                Pair("selected", selectLanguageName),
                Pair("Position", -1),
                Pair("multiSelect", true)
            )
            resultLauncher.launch(intent)
        }
    }

    private fun initInterest() {
        binding.clickInterest.setOnClickListener {
            val hash = hashMapOf<String, List<String>>()

            interestList.forEach {
                hash[it.category] = it.interest
            }

            val intent = mainNavigator.navigateToInterest(
                requireContext(),
                Pair("SelectedList", hash)
            )
            resultLauncher.launch(intent)
        }
    }

    private fun observeCountryResult() {
        getResult.observe(viewLifecycleOwner) {
            val result =
                it.getStringArrayListExtra("CountryNameList")?.toMutableList() ?: mutableListOf()
            if (result.isNotEmpty()) {
                selectedCountryName = result
                selectedCountryCode = it.getStringArrayListExtra("CountryCodeList")?.toMutableList()
                    ?: mutableListOf()
                addChipToGroup(binding.chipPreferCountry, selectedCountryName)
            }
        }
    }

    private fun observeLanguageResult() {
        getResult.observe(viewLifecycleOwner) {
            val result =
                it.getStringArrayListExtra("LanguageNameList")?.toMutableList() ?: mutableListOf()
            if (result.isNotEmpty()) {
                selectLanguageName = result
                selectLanguageCode = it.getStringArrayListExtra("LanguageCodeList")?.toMutableList()
                    ?: mutableListOf()
                addChipToGroup(binding.chipPreferLanguage, selectLanguageName)
            }
        }
    }

    private fun observerInterestResult() {
        getResult.observe(viewLifecycleOwner) {
            val result = it.intentSerializable("InterestList", HashMap::class.java)
            if (result != null) {
                interestList.clear()
                for ((key, value) in result) {
                    interestList.add(InterestVO(key as String, value as List<String>))
                }
            }
            adapter.submitList(interestList)
        }
    }

    private fun addChipToGroup(chipGroup: ChipGroup, contentList: List<String>) {
        chipGroup.removeAllViews()
        val inflater = LayoutInflater.from(context)
        contentList.forEach { content ->
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.home_chip, chipGroup, false
            ) as Chip

            chip.text = content
            chip.isCloseIconVisible = false

            chipGroup.addView(chip)
        }
    }

    private fun initAdapter() {
        adapter = makeAdapter()
        val layoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvInterest.layoutManager = layoutManager
        binding.rvInterest.adapter = adapter
    }
}