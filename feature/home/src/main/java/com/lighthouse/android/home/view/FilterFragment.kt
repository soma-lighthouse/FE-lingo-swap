package com.lighthouse.android.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.home.R
import com.lighthouse.android.home.databinding.FragmentFilterBinding
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO

class FilterFragment : BindingFragment<FragmentFilterBinding>(R.layout.fragment_filter) {
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
    }

    private fun initCountry() {
        binding.btnSelectCountry.setOnClickListener {
            val intent =
                mainNavigator.navigateToCountry(requireContext(), Pair("multiSelect", true))
            resultLauncher.launch(intent)
        }
    }

    private fun initLanguage() {
        binding.btnSelectLanguage.setOnClickListener {
            val intent = mainNavigator.navigateToLanguage(
                requireContext(), Pair("selected", listOf()), Pair("Position", -1)
            )
            resultLauncher.launch(intent)
        }
    }

    private fun initInterest() {
        binding.btnSelectHobby.setOnClickListener {
            val intent = mainNavigator.navigateToInterest(requireContext())
            resultLauncher.launch(intent)
        }
    }

    private fun observeCountryResult() {
        getResult.observe(viewLifecycleOwner) {
            val result = it.getSerializableExtra("CountryList") as? List<CountryVO>
            if (result != null) {
                addChipToGroup(binding.chipPreferCountry, result.map { c -> c.name })
            }
        }
    }

    private fun observeLanguageResult() {
        getResult.observe(viewLifecycleOwner) {
            val result = it.getSerializableExtra("LanguageList") as? List<LanguageVO>
            if (result != null) {
                addChipToGroup(binding.chipPreferLanguage, result.map { l -> l.name })
            }
        }
    }

    private fun observerInterestResult() {
        getResult.observe(viewLifecycleOwner) {
            val result = it.getSerializableExtra("InterestList") as? List<InterestVO>
            if (result != null) {
                addChipToGroup(binding.chipPreferHobby, result.flatMap { l -> l.interest })
            }
        }
    }

    private fun addChipToGroup(chipGroup: ChipGroup, contentList: List<String>) {
        binding.chipPreferCountry.removeAllViews()
        val inflater = LayoutInflater.from(context)
        contentList.forEach { content ->
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.home_chip, chipGroup, false
            ) as Chip

            chip.text = content
            chip.setOnCloseIconClickListener {
                chipGroup.removeView(it)
            }

            chipGroup.addView(chip)
        }
    }
}