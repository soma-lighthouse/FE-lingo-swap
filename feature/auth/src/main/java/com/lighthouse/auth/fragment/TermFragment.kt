package com.lighthouse.auth.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.util.disable
import com.lighthouse.android.common_ui.util.enable
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentTermBinding
import com.lighthouse.lighthousei18n.I18nManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TermFragment : BindingFragment<FragmentTermBinding>(R.layout.fragment_term) {

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    @Inject
    lateinit var i18nManager: I18nManager

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
        val language = i18nManager.getLocale().language
        Log.d("TESTING TERM", language)
        binding.clickPrivacy.setOnClickListener {
            val url =
                if (language == "ko") remoteConfig.getString("PRIVACY_URL") else remoteConfig.getString(
                    "PRIVACY_URL_EN"
                )
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        binding.clickService.setOnClickListener {
            val url =
                if (language == "ko") remoteConfig.getString("SERVICE_TERM_URL") else remoteConfig.getString(
                    "SERVICE_TERM_URL_EN"
                )
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