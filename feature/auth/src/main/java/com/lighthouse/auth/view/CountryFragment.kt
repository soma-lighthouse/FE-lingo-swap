package com.lighthouse.auth.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentCountryBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryFragment : BindingFragment<FragmentCountryBinding>(R.layout.fragment_country) {
    private val viewModel: AuthViewModel by viewModels()
    private var selectedItem = listOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        initStart()
        initCountry()
        initChip()
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initStart() {
        binding.btnStart.setOnClickListener {
            mainNavigator.navigateToMain(requireContext())
            requireActivity().finish()
        }
    }

    private fun initCountry() {
        binding.btnCountry.setOnClickListener {
            selectionList()
        }
        binding.clickRectangle.setOnClickListener {
            selectionList()
        }
    }

    private fun selectionList() {
        val intent =
            mainNavigator.navigateToCountry(requireContext(), Pair("multiSelect", true))
        resultLauncher.launch(intent)
    }

    private fun initChip() {
        getResult.observe(viewLifecycleOwner) {
            selectedItem = it.getStringArrayListExtra("CountryList")?.toList() ?: listOf()
            binding.chipCountry.apply {
                removeAllViews()
                val inflater = LayoutInflater.from(requireContext())
                selectedItem.forEach { country ->
                    val chip = inflater.inflate(
                        com.lighthouse.android.common_ui.R.layout.home_chip, this, false
                    ) as Chip

                    chip.text = country
                    chip.setOnCloseIconClickListener { view ->
                        selectedItem.minus(country)
                        removeView(view)
                    }
                    addView(chip)
                }
            }
        }
    }

}