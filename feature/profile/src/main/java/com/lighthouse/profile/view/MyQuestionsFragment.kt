package com.lighthouse.profile.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.domain.entity.response.vo.MyQuestionsVO
import com.lighthouse.profile.R
import com.lighthouse.profile.databinding.FragmentMyQuestionsBinding
import com.lighthouse.profile.databinding.MyQuestionTileBinding
import com.lighthouse.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyQuestionsFragment :
    BindingFragment<FragmentMyQuestionsBinding>(R.layout.fragment_my_questions) {
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<MyQuestionsVO, MyQuestionTileBinding>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getQuestionList()
        initAdapter()
        initBack()
    }

    private fun initAdapter() {
        adapter = makeAdapter(
            resources.getStringArray(com.lighthouse.android.common_ui.R.array.tab_name).toList()
        )
        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvMyQuestions.layoutManager = linearLayoutManager
        binding.rvMyQuestions.adapter = adapter
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getQuestionList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detail.collect {
                    render(it)
                }
            }
        }
        viewModel.getMyQuestions()
    }

    private fun render(uiState: UiState) {
        Log.d("TESTING", uiState.toString())
        when (uiState) {
            UiState.Loading -> {
                binding.pbMyQuestion.setVisible()
                binding.rvMyQuestions.setGone()
            }

            is UiState.Success<*> -> {
                binding.rvMyQuestions.setVisible()
                val data = uiState.data as List<MyQuestionsVO>
                adapter.submitList(data)
                binding.pbMyQuestion.setGone()

            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbMyQuestion.setGone()
            }
        }
    }
}

private fun makeAdapter(categoryList: List<String>) =
    SimpleListAdapter<MyQuestionsVO, MyQuestionTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onItemsTheSame = { old, new -> old.questionId == new.questionId },
            onContentsTheSame = { old, new -> old == new }
        ),
        layoutId = R.layout.my_question_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding
            binding.tvCategory.text = categoryList[item.categoryId]
            binding.tvLike.text = item.likes.toString()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")

            val dateTime = LocalDateTime.parse(item.createAt, formatter)
            binding.tvCreateAt.text = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    )