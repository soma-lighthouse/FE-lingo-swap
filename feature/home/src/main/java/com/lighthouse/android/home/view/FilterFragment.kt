package com.lighthouse.android.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.home.R
import com.lighthouse.android.home.databinding.FragmentFilterBinding

class FilterFragment : Fragment() {
    private lateinit var binding: FragmentFilterBinding
    private val contentList = listOf("Korea", "Japan", "America", "China")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        addChipToGroup(binding.chipPreferCountry, contentList)

        return binding.root
    }

    private fun addChipToGroup(chipGroup: ChipGroup, contentList: List<String>) {
        val inflater = LayoutInflater.from(context)
        contentList.forEach { content ->
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.home_chip,
                chipGroup,
                false
            ) as Chip

            chip.text = content
            chip.setOnCloseIconClickListener {
                chipGroup.removeView(it)
            }

            chipGroup.addView(chip)
        }
    }
}