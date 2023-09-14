package com.lighthouse.profile.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO
import com.lighthouse.profile.R
import com.lighthouse.profile.databinding.FragmentMyQuestionsBinding
import com.lighthouse.profile.databinding.MyQuestionTileBinding
import com.lighthouse.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class MyQuestionsFragment :
    BindingFragment<FragmentMyQuestionsBinding>(R.layout.fragment_my_questions) {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var adapter: SimpleListAdapter<BoardQuestionVO, MyQuestionTileBinding>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getQuestionList()
        initAdapter()
    }

    private fun initAdapter() {
        adapter = makeAdapter(
            resources.getStringArray(com.lighthouse.android.common_ui.R.array.tab_name).toList()
        )
        binding.rvMyQuestions.adapter = adapter
    }

    private fun getQuestionList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getMyQuestions()
                viewModel.detail.collect {
                    render(it)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            UiState.Loading -> {
                binding.pbMyQuestion.setVisible()
                binding.rvMyQuestions.setGone()
            }

            is UiState.Success<*> -> {
                binding.rvMyQuestions.setVisible()
                val data = uiState.data as List<BoardQuestionVO>
                adapter.submitList(data)
                binding.pbMyQuestion.setGone()

            }

            is UiState.Error<*> -> {
                binding.pbMyQuestion.setGone()
            }
        }
    }
}

private fun makeAdapter(categoryList: List<String>) =
    SimpleListAdapter<BoardQuestionVO, MyQuestionTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onItemsTheSame = { old, new -> old.questionId == new.questionId },
            onContentsTheSame = { old, new -> old == new }
        ),
        layoutId = R.layout.my_question_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding
            binding.tvCategory.text = categoryList[item.categoryId]
        }
    )