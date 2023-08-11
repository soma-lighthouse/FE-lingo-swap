package com.lighthouse.android.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.UserInfoTileBinding
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.android.home.adapter.makeAdapter
import com.lighthouse.android.home.databinding.FragmentHomeBinding
import com.lighthouse.android.home.util.homeTitle
import com.lighthouse.android.home.viewmodel.HomeViewModel
import com.lighthouse.domain.response.vo.ProfileVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor() :
    BindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<ProfileVO, UserInfoTileBinding>
    private val profileList = mutableListOf<ProfileVO>()
    private var next: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initScrollListener()
        initFab()
        if (profileList.isEmpty()) {
            profileList.addAll(viewModel.getUserProfiles())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchNextPage(1, 200).collect {
                    render(it)
                }
            }
        }

        lifecycleScope.launch {
            binding.tvHomeTitle.text =
                SpannableStringBuilderProvider.getSpannableBuilder(homeTitle, requireContext())
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveUserProfiles(profileList)
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                if (profileList.isEmpty()) {
                    binding.pbHomeLoading.setVisible()
                    binding.rvHome.setGone()
                    binding.fabFilter.setGone()
                }
            }

            is UiState.Success<*> -> {
                binding.rvHome.setVisible()
                Log.d("MYTAG", profileList.toString())
                profileList.addAll(uiState.data as List<ProfileVO>)
                adapter.submitList(profileList)
                binding.pbHomeLoading.setGone()
                binding.fabFilter.setVisible()
            }

            is UiState.Error -> {
                context.toast(uiState.message)
                binding.pbHomeLoading.setGone()
            }
        }
    }


    private fun initAdapter() {
        adapter = makeAdapter() { userId ->
            mainNavigator.navigateToProfile(
                context = requireContext(),
                userId = Pair("userId", userId),
                isMe = Pair("isMe", false)
            )
        }
        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvHome.layoutManager = linearLayoutManager
        binding.rvHome.adapter = adapter
    }

    private fun initScrollListener() {
        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val rvPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                if (rvPosition == totalCount && viewModel.page.value != -1) {
                    loadMoreProfiles()
                }
            }
        })
    }

    private fun loadMoreProfiles() {
        viewModel.loading.value = true
        lifecycleScope.launch {
            viewModel.fetchNextPage(1, 200).collect {
                render(it)
                viewModel.loading.value = false
            }
        }
    }

    private fun initFab() {
        binding.fabFilter.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFilterFragment())

        }
    }
}