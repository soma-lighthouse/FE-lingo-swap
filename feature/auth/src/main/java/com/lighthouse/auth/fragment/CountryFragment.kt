package com.lighthouse.auth.fragment

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
        initObserve()
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeUpload()
                observeRegister()
            }
        }

    }

    private fun initStart() {
        binding.btnStart.setOnClickListener {
            if (!validateInput()) return@setOnClickListener
            viewModel.registerInfo.preferredCountries = selectedCountryCode
            binding.groupCountry.isClickable = false
            binding.pbStart.setVisible()
            val contentUri = viewModel.profilePath?.let { Uri.parse(it) }
            val filePath = contentUri?.let { getRealPathFromUri(it) }
            if (filePath != null) {
                viewModel.uploadImg(filePath)
            } else {
                viewModel.registerUser()
            }
        }
    }

    private suspend fun observeUpload() {
        viewModel.upload.drop(1).collect {
            Log.d("REGISTER", "observeUpload: $it")
            if (it) {
                viewModel.registerUser()
            } else {
                flowOf(getString(com.lighthouse.android.common_ui.R.string.upload_error))
            }

        }
    }


    private suspend fun observeRegister() {
        viewModel.register.drop(1).collect {
            Log.d("REGISTER", "observeRegister: $it")
            registerComplete(it)
        }
    }

    private fun registerComplete(result: Any?) {
        binding.groupCountry.isClickable = true
        binding.pbStart.setGone()
        if (result == true) {
            mainNavigator.navigateToMain(requireContext())
            requireActivity().finish()
        } else {
            context.toast(result.toString())
        }
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