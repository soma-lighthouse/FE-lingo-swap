package com.lighthouse.board

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.QuestionTileBinding
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.disable
import com.lighthouse.android.common_ui.util.disableTabForSeconds
import com.lighthouse.android.common_ui.util.enable
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.board.adapter.makeAdapter
import com.lighthouse.board.databinding.FragmentBoardBinding
import com.lighthouse.board.viewmodel.BoardViewModel
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardFragment : BindingFragment<FragmentBoardBinding>(R.layout.fragment_board) {
    private val viewModel: BoardViewModel by viewModels()
    private var questionList = mutableListOf<BoardQuestionVO>()
    private lateinit var adapter: SimpleListAdapter<BoardQuestionVO, QuestionTileBinding>
    private var tabPosition = 0

    private var loading = false
    private var start = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpinner()
        initTab()
        initFab()
        initScrollListener()
        initAdapter()
        loadMoreProfiles()
        initRefresh()
    }

    private fun initRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.next[tabPosition] = null
            questionList.clear()
            viewModel.fetchState(tabPosition, null)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }


    override fun onResume() {
        super.onResume()
        if (viewModel.getQuestion(tabPosition).isNullOrEmpty()) {
            viewModel.fetchState(tabPosition, null)
        } else {
            questionList.add(BoardQuestionVO())
            viewModel.getQuestion(tabPosition)?.let { questionList.addAll(it) }
            adapter.submitList(questionList)
            binding.rvBoard.setVisible()
            binding.fabAdd.setVisible()
            binding.pbBoardLoading.setGone()
        }
    }

    private fun initSpinner() {
        val arrayList = arrayListOf(
            resources.getString(com.lighthouse.android.common_ui.R.string.sort_latest),
            resources.getString(com.lighthouse.android.common_ui.R.string.sort_top_rated)
        )
        val arrayAdapter = ArrayAdapter(
            requireContext(), com.lighthouse.android.common_ui.R.layout.spinner_item, arrayList
        ).apply {
            setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        }

        binding.spinnerSort.adapter = arrayAdapter
    }

    private fun initTab() {
        resources.getStringArray(com.lighthouse.android.common_ui.R.array.tab_name).forEach {
            binding.tabBoard.addTab(binding.tabBoard.newTab().setText(it))
        }

        binding.tabBoard.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                questionList.clear()
                adapter.notifyDataSetChanged()
                start = true
                tabPosition = tab?.position ?: 0
                if (!viewModel.getQuestion(tabPosition).isNullOrEmpty()) {
                    viewModel.next[tabPosition] = viewModel.getFirstNext(tabPosition)
                    questionList.add(BoardQuestionVO())
                    viewModel.getQuestion(tabPosition)?.let { questionList.addAll(it) }

                    adapter.submitList(questionList)
                } else {
                    viewModel.next[tabPosition] = null
                    viewModel.fetchState(tabPosition, null)
                }
                binding.tabBoard.isClickable = false
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // TODO("Not yet implemented")
            }
        })
    }

    private fun initAdapter() {
        adapter = makeAdapter(requireContext(), { questionId ->
            viewModel.updateLike(questionId)
            viewModel.setLike(tabPosition, questionId, true)
        }, { questionId ->
            viewModel.cancelLike(questionId)
            viewModel.setLike(tabPosition, questionId, false)
        }, { userId ->
            mainNavigator.navigateToProfile(
                requireContext(), Pair("userId", userId), Pair("isMe", false)
            )
        })

        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvBoard.layoutManager = linearLayoutManager
        binding.rvBoard.adapter = adapter
    }

    private fun initScrollListener() {
        binding.rvBoard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val rvPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                if (rvPosition == totalCount && viewModel.page != -1 && !loading) {
                    loading = true
                    viewModel.fetchState(tabPosition, null)
                }
            }
        })
    }

    private fun loadMoreProfiles() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect {
                    render(it)
                }
            }
        }
    }


    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                if (questionList.isEmpty() && start) {
                    binding.pbBoardLoading.setVisible()
                    binding.rvBoard.setGone()
                    binding.tabBoard.disable()
                    binding.fabAdd.setGone()
                    start = false
                }
            }

            is UiState.Success<*> -> {
                questionList.add(BoardQuestionVO())
                questionList.addAll(uiState.data as List<BoardQuestionVO>)
                Log.d("QUESTION", questionList.size.toString())
                adapter.submitList(questionList)
                binding.tabBoard.enable()
                disableTabForSeconds(2) {
                    bindingWeakRef?.get()?.let { b ->
                        b.rvBoard.setVisible()
                        b.pbBoardLoading.setGone()
                        b.fabAdd.setVisible()
                        loading = false
                    }
                }
                viewModel.clearResult()
            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbBoardLoading.setGone()
            }
        }
    }

    private fun initFab() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(BoardFragmentDirections.actionBoardFragmentToAddFragment())
        }
    }
}