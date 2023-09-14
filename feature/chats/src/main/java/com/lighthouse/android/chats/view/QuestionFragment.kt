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
import androidx.recyclerview.widget.RecyclerView
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
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionFragment : Fragment() {
    private val viewModel: ChatViewModel by activityViewModels()
    private lateinit var binding: FragmentQuestionBinding
    private lateinit var adapter: SimpleListAdapter<BoardQuestionVO, ChatQuestionTileBinding>

    private val questionList = mutableListOf<BoardQuestionVO>()
    private var start = true
    private var curPosition = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false)
        val categories =
            resources.getStringArray(com.lighthouse.android.common_ui.R.array.tab_name).toList()
        addChipToGroup(binding.chipCategory, categories)
        initScrollListener()
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
            questionList.clear()
            curPosition = (checkedIds.first() - 1) % num + 1

            viewModel.next[curPosition] = null
            fetchQuestion(curPosition)
        }
    }

    private fun fetchQuestion(category: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getQuestion(category, null).collect {
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

                if (rvPosition == totalCount && viewModel.page != -1) {
                    loadMoreProfiles()
                }
            }
        })
    }

    private fun loadMoreProfiles() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getQuestion(curPosition, null).collect {
                    render(it)
                }
            }
        }
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
                binding.rvQuestionPanel.setVisible()
                questionList.addAll(uiState.data as List<BoardQuestionVO>)
                adapter.submitList(questionList)
                binding.pbQuestionLoading.setGone()
            }

            is UiState.Error<*> -> {
                context.toast(uiState.message.toString())
                binding.pbQuestionLoading.setGone()
            }
        }
    }


}