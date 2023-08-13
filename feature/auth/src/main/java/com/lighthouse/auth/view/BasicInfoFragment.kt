package com.lighthouse.auth.view

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.auth.databinding.FragmentBasicInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasicInfoFragment :
    BindingFragment<FragmentBasicInfoBinding>(com.lighthouse.auth.R.layout.fragment_basic_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpinner()
        initInterest()
        initCountry()
        initNext()
    }

    private fun initNext() {
        binding.btnNext.setOnClickListener {
            findNavController().navigate(BasicInfoFragmentDirections.actionInfoFragmentToLanguageFragment())
        }
    }

    private fun initSpinner() {
        val arrayList = arrayListOf(
            resources.getString(R.string.gender),
            resources.getString(R.string.male),
            resources.getString(R.string.female),
            resources.getString(R.string.rather_not_tell)
        )
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            arrayList
        ).apply {
            setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        }

        binding.spinnerGender.adapter = arrayAdapter
    }

    private fun initInterest() {
        binding.btnInterest.setOnClickListener {
            val intent = mainNavigator.navigateToInterest(requireContext())
            resultLauncher.launch(intent)
        }
    }

    private fun initCountry() {
        binding.btnNation.setOnClickListener {
            val intent = mainNavigator.navigateToCountry(requireContext())
            resultLauncher.launch(intent)
        }
    }


}