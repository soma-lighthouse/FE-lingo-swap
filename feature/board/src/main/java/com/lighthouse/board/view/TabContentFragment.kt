package com.lighthouse.board.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.constant.setGone
import com.lighthouse.android.common_ui.constant.setVisible
import com.lighthouse.android.common_ui.constant.toast
import com.lighthouse.android.common_ui.databinding.QuestionTileBinding
import com.lighthouse.board.R
import com.lighthouse.board.adapter.makeAdapter
import com.lighthouse.board.databinding.FragmentTabContentBinding
import com.lighthouse.board.util.UiState
import com.lighthouse.board.util.UiState.Error
import com.lighthouse.board.util.UiState.Loading
import com.lighthouse.board.util.UiState.Success
import com.lighthouse.board.viewmodel.BoardViewModel
import com.lighthouse.domain.response.vo.BoardQuestionVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TabContentFragment : Fragment() {
    private val viewModel: BoardViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private lateinit var binding: FragmentTabContentBinding
    private lateinit var adapter: SimpleListAdapter<BoardQuestionVO, QuestionTileBinding>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_content, container, false)
        initAdapter()
        initRefresh()

        val curPos = arguments?.getInt("tab_pos") ?: "Default Content"
        val order = arguments?.getString("order") ?: "date"

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchState(curPos as Int, "latest").collect {
                    render(it)
                }
            }
        }


        return binding.root
    }

    private fun initRefresh() {
        binding.srBoard.setOnRefreshListener {
            Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show()
            binding.srBoard.isRefreshing = false
        }
    }

    private fun initAdapter() {
        adapter = makeAdapter() { questionId, memberId ->
            viewModel.updateLike(questionId, memberId)
        }

        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvQuestion.layoutManager = linearLayoutManager
        binding.rvQuestion.adapter = adapter
    }


    private fun render(uiState: UiState) {
        when (uiState) {
            is Loading -> {
                binding.pbBoardLoading.setVisible()
                binding.srBoard.setGone()

            }

            is Success<*> -> {
                binding.srBoard.setVisible()
                adapter.submitList(uiState.data as List<BoardQuestionVO>)
                binding.pbBoardLoading.setGone()
            }

            is Error -> {
                context.toast(uiState.message)
                binding.pbBoardLoading.setGone()
            }
        }
    }

}