package com.lighthouse.auth.view

import android.os.Bundle
import android.view.View
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.auth.R
import com.lighthouse.auth.adapter.CountryAdapter
import com.lighthouse.auth.databinding.FragmentCountryBinding
import com.lighthouse.domain.entity.response.vo.CountryVO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryFragment : BindingFragment<FragmentCountryBinding>(R.layout.fragment_country) {
    private lateinit var adapter: CountryAdapter
    private val countryList = mutableListOf<CountryVO>()
    private var selectedItem = mutableListOf<CountryVO>()

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
            val intent = mainNavigator.navigateToCountry(requireContext())
            resultLauncher.launch(intent)
        }
    }

    private fun initChip() {
        // TODO()
    }

}