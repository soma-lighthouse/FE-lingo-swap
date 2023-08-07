package com.lighthouse.board.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.common_ui.constant.toast
import com.lighthouse.board.R
import com.lighthouse.board.databinding.FragmentAddBinding
import com.lighthouse.board.viewmodel.BoardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : Fragment() {
    private val viewModel: BoardViewModel by viewModels()
    private lateinit var binding: FragmentAddBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)
        initBack()
        initAddButton()

        val interestList = listOf(
            requireContext().resources.getString(com.lighthouse.android.common_ui.R.string.cooking),
            requireContext().resources.getString(com.lighthouse.android.common_ui.R.string.game),
            requireContext().resources.getString(com.lighthouse.android.common_ui.R.string.movie),
            requireContext().resources.getString(com.lighthouse.android.common_ui.R.string.music),
            requireContext().resources.getString(com.lighthouse.android.common_ui.R.string.travel),
            requireContext().resources.getString(com.lighthouse.android.common_ui.R.string.study),
        )

        addChipToGroup(binding.chipInterest, interestList)

        return binding.root
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun addChipToGroup(chipGroup: ChipGroup, interestList: List<String>) {
        val inflater = LayoutInflater.from(context)
        interestList.forEach {
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.chip, binding.chipInterest, false
            ) as Chip

            chip.text = it
            chip.isCloseIconVisible = false
            if (it == interestList[0]) {
                chip.isChecked = true
            }

            chipGroup.isSingleSelection = true
            chipGroup.isSelectionRequired = true
            chipGroup.addView(chip)
        }
    }

    private fun initAddButton() {
        binding.btnAdd.setOnClickListener {
            val text = binding.etQuestion.text.toString()
            if (text.length <= 10 || text.length >= 200) {
                val msg =
                    requireContext().resources.getString(com.lighthouse.android.common_ui.R.string.question_size_error)
                context.toast(msg)
            } else {
                val categoryId = binding.chipInterest.checkedChipId - 2
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.uploadQuestion(1, categoryId, text).collect {

                    }

                }
            }
        }
    }
}