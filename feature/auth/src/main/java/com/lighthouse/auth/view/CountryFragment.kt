package com.lighthouse.auth.view

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
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentCountryBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.CountryVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CountryFragment : BindingFragment<FragmentCountryBinding>(R.layout.fragment_country) {
    private val viewModel: AuthViewModel by activityViewModels()
    private var selectedItem = listOf<CountryVO>()

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
                viewModel.registerInfo.preferredCountries = selectedItem.map { it.code }
                viewLifecycleOwner.lifecycleScope.launch {
                    val contentUri = Uri.parse(viewModel.profilePath)
                    val filePath = getRealPathFromUri(contentUri)
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        if (viewModel.profilePath != null) {
                            async {
                                if (filePath != null) {
                                    viewModel.uploadImg(filePath).collect {
                                        Log.d("PICTURE", it.toString())
                                    }
                                }
                            }.await()
                        }
                        viewModel.registerUser().collect {
                            if (it == true) {
                                mainNavigator.navigateToMain(requireContext())
                                requireActivity().finish()
                            } else {
                                context.toast(it.toString())
                            }
                        }
                    }
                }
            } else {
                context.toast(resources.getString(com.lighthouse.android.common_ui.R.string.invalid_null))
            }
        }
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        var realPath: String? = null
        if (uri.scheme == "file") {
            realPath = uri.path
        } else if (uri.scheme == "content") {
            val cursor =
                requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (it.moveToFirst()) {
                    return it.getString(columnIndex)
                }
            }
        }
        return realPath
    }


    private fun validateInput() = if (selectedItem.isEmpty()) {
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
        val intent = mainNavigator.navigateToCountry(requireContext(), Pair("multiSelect", true))
        resultLauncher.launch(intent)
    }

    private fun initChip() {
        getResult.observe(viewLifecycleOwner) {
            selectedItem = it.getSerializableExtra("CountryList") as List<CountryVO>
            binding.chipCountry.apply {
                removeAllViews()
                val inflater = LayoutInflater.from(requireContext())
                selectedItem.forEach { country ->
                    val chip = inflater.inflate(
                        com.lighthouse.android.common_ui.R.layout.home_chip, this, false
                    ) as Chip

                    chip.text = country.name
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