package com.lighthouse.auth.fragment

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentCountryBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.flatMapLatest
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
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initStart() {
        binding.btnStart.setOnClickListener {
            if (validateInput()) {
                viewModel.registerInfo.preferredCountries = selectedCountryCode
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        if (viewModel.profilePath != null) {
                            val contentUri = Uri.parse(viewModel.profilePath)
                            val filePath = getRealPathFromUri(contentUri)
                            if (filePath != null) {
                                viewModel.uploadImg(filePath).flatMapLatest { uploadResult ->
                                    if (uploadResult == true) {
                                        binding.groupCountry.isClickable = false
                                        binding.pbStart.setVisible()
                                        viewModel.registerUser()
                                    } else {
                                        flowOf(false)
                                    }
                                }.collect { result ->
                                    binding.groupCountry.isClickable = true
                                    binding.pbStart.setGone()
                                    registerComplete(result)
                                }
                            }
                        } else {
                            viewModel.registerUser().collect { result ->
                                registerComplete(result)
                            }
                        }
                    }
                }

            }
        }
    }

    private fun registerComplete(result: Any?) = if (result == true) {
        mainNavigator.navigateToMain(requireContext())
        requireActivity().finish()
    } else {
        context.toast(result.toString())
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        var realPath: String? = null
        if (uri.scheme == "file") {
            realPath = uri.path
        } else if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (it.moveToFirst()) {
                    return it.getString(columnIndex)
                }
            }
        }
        return realPath
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