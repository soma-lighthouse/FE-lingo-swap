package com.lighthouse.android.chats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.chats.R
import com.lighthouse.android.chats.adapter.makeAdapter
import com.lighthouse.android.chats.databinding.ChatQuestionTileBinding
import com.lighthouse.android.chats.databinding.FragmentQuestionBinding
import com.lighthouse.android.chats.viewmodel.ChatViewModel
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionFragment : Fragment() {
    private val viewModel: ChatViewModel by activityViewModels()
    private lateinit var binding: FragmentQuestionBinding
    private lateinit var adapter: SimpleListAdapter<BoardQuestionVO, ChatQuestionTileBinding>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false)
        val categories =
            resources.getStringArray(com.lighthouse.android.common_ui.R.array.tab_name).toList()
        addChipToGroup(
            binding.chipCategory, categories
        )
        initChip(categories.size)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        fetchQuestion(1)
    }

    private fun initAdapter() {
        adapter = makeAdapter() {
            viewModel.question.value = it
        }
        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvQuestionPanel.layoutManager = linearLayoutManager
        binding.rvQuestionPanel.adapter = adapter
    }

    private fun addChipToGroup(chipGroup: ChipGroup, interestList: List<String>) {
        val inflater = LayoutInflater.from(context)
        interestList.forEach {
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.chip, binding.chipCategory, false
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

    private fun initChip(num: Int) {
        binding.chipCategory.setOnCheckedStateChangeListener { group, checkedIds ->
            val category = (checkedIds.first() - 1) % num + 1
            fetchQuestion(category)
        }
    }

    private fun fetchQuestion(category: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getQuestion(category, null, 1).collect {
                    render(it)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                // TODO()

            }

            is UiState.Success<*> -> {
                adapter.submitList(uiState.data as List<BoardQuestionVO>)
            }

            is UiState.Error -> {
                context.toast(uiState.message)
                binding.pbQuestionLoading.setGone()
            }
        }
    }


}