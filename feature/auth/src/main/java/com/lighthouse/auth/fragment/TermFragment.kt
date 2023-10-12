package com.lighthouse.auth.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.util.disable
import com.lighthouse.android.common_ui.util.enable
import com.lighthouse.auth.BuildConfig
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentTermBinding

class TermFragment : BindingFragment<FragmentTermBinding>(R.layout.fragment_term) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        openBrowser()
        checkAgreeResult()
        initNext()
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun openBrowser() {
        binding.clickPrivacy.setOnClickListener {
            val url = BuildConfig.PRIVACY_TERM_URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        binding.clickService.setOnClickListener {
            val url = BuildConfig.SERVICE_TERM_URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun checkAgreeResult() {
        binding.cbServiceCheck.setOnCheckedChangeListener { _, _ ->
            updateButton()
        }

        binding.cbServiceCheck2.setOnCheckedChangeListener { _, _ ->
            updateButton()
        }
    }

    private fun updateButton() {
        if (binding.cbServiceCheck.isChecked && binding.cbServiceCheck2.isChecked) {
            binding.btnNext.enable()
            binding.btnNext.setBackgroundResource(com.lighthouse.android.common_ui.R.drawable.custom_button)
        } else {
            binding.btnNext.disable()
            binding.btnNext.setBackgroundResource(com.lighthouse.android.common_ui.R.drawable.custom_button_disable)
        }
    }

    private fun initNext() {
        binding.btnNext.setBackgroundResource(com.lighthouse.android.common_ui.R.drawable.custom_button_disable)
        binding.btnNext.disable()
        binding.btnNext.setOnClickListener {
            findNavController().navigate(TermFragmentDirections.actionTermFragmentToInfoFragment())
        }
    }
}