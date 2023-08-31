package com.lighthouse.board.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.QuestionTileBinding
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.board.R
import com.lighthouse.board.adapter.makeAdapter
import com.lighthouse.board.databinding.FragmentTabContentBinding
import com.lighthouse.board.viewmodel.BoardViewModel
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TabContentFragment :
    BindingFragment<FragmentTabContentBinding>(R.layout.fragment_tab_content) {
    private val viewModel: BoardViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private lateinit var adapter: SimpleListAdapter<BoardQuestionVO, QuestionTileBinding>

    private val questionList = mutableListOf<BoardQuestionVO>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initRefresh()
        initBoard()
        initScrollListener()
    }

    override fun onResume() {
        super.onResume()
        adapter.submitList(questionList)
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveQuestion(questionList)
    }

    private fun initBoard() {
        val curPos = arguments?.getInt("tab_pos") ?: "Default Content"
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                if (questionList.isEmpty()) {
                    viewModel.fetchState(curPos as Int, null).collect {
                        render(it)
                    }
                }
            }
        }
    }

    private fun initRefresh() {
        binding.srBoard.setOnRefreshListener {
            Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show()
            binding.srBoard.isRefreshing = false
        }
    }

    private fun initAdapter() {
        adapter = makeAdapter({ questionId, userId ->
            viewModel.updateLike(questionId, userId)
        }, { questionId, userId ->
            viewModel.cancelLike(questionId, userId)
        }, { userId ->
            mainNavigator.navigateToProfile(
                requireContext(),
                Pair("userId", userId),
                Pair("isMe", false)
            )
        })

        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvQuestion.layoutManager = linearLayoutManager
        binding.rvQuestion.adapter = adapter
    }

    private fun initScrollListener() {
        binding.rvQuestion.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        val curPos = arguments?.getInt("tab_pos") ?: "Default Content"
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchState(curPos as Int, null).collect {
                    render(it)
                }
            }
        }
    }


    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbBoardLoading.setVisible()
                binding.srBoard.setGone()

            }

            is UiState.Success<*> -> {
                binding.srBoard.setVisible()
                questionList.addAll(uiState.data as List<BoardQuestionVO>)
                Log.d("QUESTION", questionList.size.toString())
                adapter.submitList(questionList)
                binding.pbBoardLoading.setGone()
            }

            is UiState.Error -> {
                context.toast(uiState.message)
                binding.pbBoardLoading.setGone()
            }
        }
    }

}