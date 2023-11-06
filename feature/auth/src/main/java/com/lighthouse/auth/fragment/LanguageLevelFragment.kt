package com.lighthouse.auth.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.auth.LanguageNavGraphDirections
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.FragmentLanguageLevelBinding
import com.lighthouse.auth.selection_adapter.SelectionAdapter
import com.lighthouse.auth.view.LanguageListActivity
import com.lighthouse.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageLevelFragment :
    BindingFragment<FragmentLanguageLevelBinding>(R.layout.fragment_language_level) {
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var adapter: SelectionAdapter
    private var isRegister: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        isRegister = (requireActivity() as LanguageListActivity).isRegister
        binding.isRegister = isRegister
        initBack()
        initAdapter()
        observeClick()
    }

    private fun observeClick() {
        viewModel.changes.observe(viewLifecycleOwner) {
            if (it == -1) {
                requireActivity().finish()
            }
            if (it == -3) {
                findNavController().navigate(LanguageNavGraphDirections.actionGlobalCountryFragment())
            }
            if (it == -4) {
                findNavController().navigate(LanguageLevelFragmentDirections.actionLanguageLevelFragmentToLanguagesFragment())
            }
        }

        viewModel.selectedLanguage.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            if (isRegister) {
                findNavController().navigate(LanguageNavGraphDirections.actionPopLanguageNavGraph())
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun initAdapter() {
        adapter = SelectionAdapter(
            multiSelection = false,
            context = requireContext(),
            type = SelectionAdapter.LEVEL,
            viewModel = viewModel,
        )
        adapter.submitList(viewModel.selectedLanguage.value)
        binding.rvLanguageLevel.adapter = adapter
    }
}