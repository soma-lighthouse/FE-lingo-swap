package com.lighthouse.android.home

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.UserInfoTileBinding
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.home.adapter.makeAdapter
import com.lighthouse.android.home.databinding.FragmentHomeBinding
import com.lighthouse.android.home.util.UiState
import com.lighthouse.android.home.util.homeTitle
import com.lighthouse.android.home.util.setGone
import com.lighthouse.android.home.util.setVisible
import com.lighthouse.android.home.util.toast
import com.lighthouse.android.home.viewmodel.HomeViewModel
import com.lighthouse.domain.response.dto.ProfileVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: SimpleListAdapter<ProfileVO, UserInfoTileBinding>
    private val profileList = mutableListOf<ProfileVO>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        initAdapter()
        initScrollListener()
        if (profileList.isEmpty()) {
            profileList.addAll(viewModel.getUserProfiles())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect {
                    render(it)
                }
            }
        }

        lifecycleScope.launch {
            binding.tvHomeTitle.text =
                SpannableStringBuilderProvider.getSpannableBuilder(homeTitle, requireContext())
        }

        binding.fabFilter.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFilterFragment())

        }

        viewModel.loading.observe(viewLifecycleOwner) {
            adapter.showLoading(it)

        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveUserProfiles(profileList)
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbHomeLoading.setVisible()
                binding.rvHome.setGone()
                binding.fabFilter.setGone()
            }

            is UiState.Success -> {
                binding.rvHome.setVisible()
                if (profileList.isEmpty()) {
                    profileList.addAll(uiState.profiles)
                }
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
        adapter = makeAdapter()
        binding.rvHome.adapter = adapter
    }

    private fun initScrollListener() {
        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val rvPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                val totalCount =
                    recyclerView.adapter?.itemCount?.minus(1)

                if (rvPosition == totalCount && viewModel.page.value != -1 && viewModel.page.value <= 10) {
                    loadMoreProfiles()
                }
            }
        })
    }

    private fun loadMoreProfiles() {
        viewModel.loading.value = true

        lifecycleScope.launch {
            viewModel.fetchNextPage().collect {
                it?.let {
                    profileList.addAll(it)
                    adapter.submitList(profileList)
                    viewModel.loading.value = false
                }
            }
        }
    }
}