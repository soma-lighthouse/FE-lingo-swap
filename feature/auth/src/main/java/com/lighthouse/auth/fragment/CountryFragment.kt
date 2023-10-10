package com.lighthouse.auth.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.util.UriUtil
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentCountryBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CountryFragment : BindingFragment<FragmentCountryBinding>(R.layout.fragment_country) {
    private val viewModel: AuthViewModel by activityViewModels()
    private var selectedCountryName = mutableListOf<String>()
    private var selectedCountryCode = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        initStart()
        initCountry()
        initChip()
        observeUpload()
        observeRegister()

    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initStart() {
        binding.btnStart.setOnClickListener {
            if (!validateInput()) return@setOnClickListener
            viewModel.registerInfo.preferredCountries = selectedCountryCode
            binding.groupCountry.isClickable = false
            binding.pbStart.setVisible()
            val contentUri = viewModel.profilePath?.let { Uri.parse(it) }
            val filePath = contentUri?.let { UriUtil.getRealPath(requireContext(), it) }
            if (filePath != null) {
                viewModel.uploadImg(filePath)
            } else {
                viewModel.registerUser()
            }
        }
    }

    private fun observeUpload() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.upload.drop(1).collect {
                    if (it) {
                        viewModel.registerUser()
                    } else {
                        flowOf(getString(com.lighthouse.android.common_ui.R.string.upload_error))
                    }
                }
            }
        }
    }


    private fun observeRegister() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect {
                    registerComplete(it)
                }
            }
        }
    }

    private fun registerComplete(result: Boolean) {
        Log.d("TESTING4", "registerComplete: $result")
        binding.groupCountry.isClickable = true
        binding.pbStart.setGone()
        if (result) {
            val intent = mainNavigator.navigateToMain(
                requireContext(),
                Pair("NewChat", false),
                Pair("ChannelxId", "")
            )
            startActivity(intent)
            requireActivity().finish()
        }
    }


    private fun validateInput() = if (selectedCountryCode.isEmpty()) {
        binding.clickRectangle.setBackgroundResource(com.lighthouse.android.common_ui.R.drawable.error_box)
        false
    } else {
        binding.clickRectangle.setBackgroundResource(0)
        true
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
        val intent = mainNavigator.navigateToCountry(
            requireContext(), Pair("multiSelect", true), Pair("SelectedList", selectedCountryName)
        )
        resultLauncher.launch(intent)
    }

    private fun initChip() {
        getResult.observe(viewLifecycleOwner) {
            selectedCountryName =
                it.getStringArrayListExtra("CountryNameList")?.toMutableList() ?: mutableListOf()
            selectedCountryCode =
                it.getStringArrayListExtra("CountryCodeList")?.toMutableList() ?: mutableListOf()
            binding.chipCountry.apply {
                removeAllViews()
                val inflater = LayoutInflater.from(requireContext())
                selectedCountryName.forEach { country ->
                    val chip = inflater.inflate(
                        com.lighthouse.android.common_ui.R.layout.home_chip, this, false
                    ) as Chip

                    chip.text = country
                    chip.setOnCloseIconClickListener { view ->
                        val pos = selectedCountryName.indexOf(country)
                        selectedCountryName.removeAt(pos)
                        selectedCountryCode.removeAt(pos)
                        removeView(view)
                    }
                    addView(chip)
                }
            }
        }
    }

}