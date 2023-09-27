package com.lighthouse.android.chats.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.chats.R
import com.lighthouse.android.chats.adapter.makeAdapter
import com.lighthouse.android.chats.databinding.ChatQuestionTileBinding
import com.lighthouse.android.chats.databinding.FragmentQuestionBinding
import com.lighthouse.android.chats.viewmodel.ChatViewModel
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.disableTabForSeconds
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionFragment : BindingFragment<FragmentQuestionBinding>(R.layout.fragment_question) {
    private val viewModel: ChatViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<String, ChatQuestionTileBinding>

    private val questionList = mutableListOf<String>()
    private var start = true
    private var curPosition = 1
    private var loading = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categories =
            resources.getStringArray(com.lighthouse.android.common_ui.R.array.tab_name).toList()
        addChipToGroup(binding.chipCategory, categories)
        initScrollListener()
        initChip()
        initAdapter()
        observeQuestion()
    }

    private fun initAdapter() {
        adapter = makeAdapter() {
            viewModel.sendQuestion.value = it
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

            chip.id = interestList.indexOf(it) + 1

            chipGroup.isSingleSelection = true
            chipGroup.isSelectionRequired = true
            chipGroup.addView(chip)
        }
    }

    private fun initChip() {
        viewModel.getQuestion(curPosition)
        binding.chipCategory.setOnCheckedStateChangeListener { _, checkedIds ->
            Log.d("TESTING CHIPS", "enter")

            questionList.clear()
            adapter.notifyDataSetChanged()
            curPosition = checkedIds.first()
            start = true
            Log.d("TESTING", curPosition.toString())
            viewModel.next[curPosition] = null
            viewModel.getQuestion(curPosition)
            binding.chipCategory.isClickable = false
        }
    }

    private fun observeQuestion() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.question.collect {
                    render(it)
                }
            }
        }
    }

    private fun initScrollListener() {
        binding.rvQuestionPanel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val rvPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                if (rvPosition == totalCount && viewModel.page != -1 && !loading) {
                    loading = true
                    viewModel.getQuestion(curPosition)
                }
            }
        })
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                if (questionList.isEmpty() && start) {
                    binding.pbQuestionLoading.setVisible()
                    binding.rvQuestionPanel.setGone()
                    start = false
                }
            }

            is UiState.Success<*> -> {
                questionList.addAll(uiState.data as List<String>)
                adapter.submitList(questionList)
                disableTabForSeconds(1) {
                    bindingWeakRef?.get()?.let { b ->
                        b.rvQuestionPanel.setVisible()
                        b.pbQuestionLoading.setGone()
                        b.rvQuestionPanel.scrollToPosition(0)
                        b.chipCategory.isClickable = true
                        loading = false
                    }
                }
            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbQuestionLoading.setGone()
            }
        }
    }


}