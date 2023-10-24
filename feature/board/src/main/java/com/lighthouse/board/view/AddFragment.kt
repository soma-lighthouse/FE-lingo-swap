package com.lighthouse.board.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setInvisible
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.board.R
import com.lighthouse.board.databinding.FragmentAddBinding
import com.lighthouse.board.viewmodel.BoardViewModel
import com.lighthouse.domain.entity.request.UploadQuestionVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : BindingFragment<FragmentAddBinding>(R.layout.fragment_add) {
    private val viewModel: BoardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        initTab()
        observeResult()
        observeToast()
        setUpBinding()
    }

    private fun initTab() {
        addChipToGroup(
            binding.chipInterest,
            resources.getStringArray(com.lighthouse.android.common_ui.R.array.tab_name).toList()
        )
    }

    private fun setUpBinding() {
        binding.upload = UploadQuestionVO(1, 1, "")
        binding.viewModel = viewModel
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun observeToast() {
        viewModel.toast.observe(viewLifecycleOwner) {
            context.toast(it)
        }
    }

    private fun addChipToGroup(chipGroup: ChipGroup, interestList: List<String>) {
        val inflater = LayoutInflater.from(context)
        interestList.forEachIndexed { index, s ->
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.chip, binding.chipInterest, false
            ) as Chip

            chip.text = s
            chip.isCloseIconVisible = false
            chip.id = index
            if (s == interestList[0]) {
                chip.isChecked = true
            }

            chipGroup.isSingleSelection = true
            chipGroup.isSelectionRequired = true
            chipGroup.addView(chip)
        }
    }

    private fun observeResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.drop(1).collect {
                    render(it)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.apply {
                    pbAddLoading.setVisible()
                    btnAdd.isEnabled = false
                    btnBack.isEnabled = false
                }
            }

            is UiState.Success<*> -> {
                binding.apply {
                    pbAddLoading.setGone()
                    context.toast(requireContext().getString(com.lighthouse.android.common_ui.R.string.upload_success))
                    findNavController().navigate(AddFragmentDirections.actionAddFragmentToBoardFragment())
                }
            }

            is UiState.Error<*> -> {
                binding.apply {
                    pbAddLoading.setInvisible()
                    btnAdd.isEnabled = true
                    btnBack.isEnabled = true
                }
                handleException(uiState)
            }
        }
    }
}