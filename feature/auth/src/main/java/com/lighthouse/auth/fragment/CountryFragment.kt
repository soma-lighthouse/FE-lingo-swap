package com.lighthouse.auth.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.util.setGone
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        initStart()
        initCountry()
        observeUpload()
        observeRegister()
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkCountryUpdate(true)
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
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

    private fun initStart() {
        viewModel.changes.observe(viewLifecycleOwner) {
            if (it == -2) {
                registerComplete(true)
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
        binding.groupCountry.isClickable = true
        binding.pbStart.setGone()
        if (result) {
            val intent = mainNavigator.navigateToMain(
                requireContext(),
                Pair("NewChat", false),
                Pair("ChannelId", ""),
                Pair("url", "")
            )
            startActivity(intent)
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
        mainNavigator.navigateToCountry(
            requireContext(), Pair("multiSelect", true)
        )
    }
}