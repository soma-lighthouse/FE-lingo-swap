package com.lighthouse.board

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.base.BindingFragment
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
    private lateinit var adapter: SimpleListAdapter<BoardQuestionVO, QuestionTileBinding>

    private var loading = false
    private var start = true
    private val observer = Observer<Int> {
        observeTabChange()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initSpinner()
        initTab()
        initFab()
        initScrollListener()
        initAdapter()
        loadMoreProfiles()
    }


    override fun onResume() {
        super.onResume()

        if (adapter.currentList.isNotEmpty()) {
            adapter.submitList(viewModel.getQuestion())
            binding.rvBoard.setVisible()
            binding.fabAdd.setVisible()
            binding.pbBoardLoading.setGone()
        }

        viewModel.categoryId.observe(viewLifecycleOwner, observer)
        binding.tabBoard.getTabAt(viewModel.categoryId.value ?: 0)?.select()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.pbBoardLoading.setGone()
    }

    private fun observeTabChange() {
        start = true
        if (!viewModel.getQuestion().isNullOrEmpty()) {
            val questionList = viewModel.getQuestion()

            adapter.submitList(questionList)
        } else {
            viewModel.fetchState(null)
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
    }


    private fun initAdapter() {
        adapter = makeAdapter(requireContext(), viewModel) { userId ->
            mainNavigator.navigateToProfile(
                requireContext(), Pair("userId", userId), Pair("isMe", false), Pair("isChat", false)
            )
        }
        binding.rvBoard.adapter = adapter
    }

    private fun initScrollListener() {
        binding.rvBoard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val rvPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                if (rvPosition == totalCount && !loading) {
                    loading = true
                    viewModel.fetchState(null)
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
                if (viewModel.getQuestion() == null && start) {
                    binding.pbBoardLoading.setVisible()
                    binding.rvBoard.setGone()
                    binding.tabBoard.disable()
                    binding.fabAdd.setGone()
                    start = false
                }
            }

            is UiState.Success<*> -> {
                adapter.submitList(uiState.data as List<BoardQuestionVO>)
                binding.tabBoard.enable()
                disableTabForSeconds(1) {
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