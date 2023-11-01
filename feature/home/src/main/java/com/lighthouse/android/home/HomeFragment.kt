package com.lighthouse.android.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.home.adapter.makeAdapter
import com.lighthouse.android.home.databinding.FragmentHomeBinding
import com.lighthouse.android.home.viewmodel.HomeViewModel
import com.lighthouse.domain.entity.response.vo.ProfileVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor() :
    BindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: SimpleListAdapter<ProfileVO, UserInfoTileBinding>
    private var profileList = mutableListOf<ProfileVO>()
    private var next: Int? = null
    private var loading = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d("TESTING PERMISSION", "$isGranted")
            viewModel.setNotification(isGranted)
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val intent = requireActivity().intent
        binding.viewModels = viewModel
        initAdapter()
        initScrollListener()
        initFab()
        initMatch()
        checkPermission()
        redirectToDestination(
            intent.getStringExtra("baseUrl") ?: "",
            intent.getStringExtra("path") ?: ""
        )
    }

    private fun checkPermission() {
        if (!hasPermission()) {
            Log.d("TESTING PERMISSION", "NO PERMISSION")
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    private fun hasPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        profileList = viewModel.getUserProfiles().toMutableList()
        adapter.submitList(profileList)
    }

    private fun initMatch() {
        if (viewModel.getUserProfiles().isEmpty()) {
            viewModel.resetFilterState()
            viewModel.fetchNextPage()
            loading = true
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filter.collect {
                    render(it)
                }
            }
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
                if (uiState.data is List<*>) {
                    profileList.add(ProfileVO())
                    profileList.addAll(uiState.data as List<ProfileVO>)
                }
                adapter.submitList(profileList)
                binding.pbHomeLoading.setGone()
                binding.fabFilter.setVisible()
                viewModel.resetFilterState()
                loading = false
            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbHomeLoading.setGone()
            }
        }
    }


    private fun initAdapter() {
        adapter = makeAdapter(requireContext()) { userId ->
            mainNavigator.navigateToProfile(
                context = requireContext(),
                userId = Pair("userId", userId),
                isMe = Pair("isMe", false),
                isChat = Pair("isChat", false)
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

                val totalCount = recyclerView.adapter?.itemCount?.minus(3)

                if (rvPosition == totalCount && viewModel.page != -1 && !loading) {
                    viewModel.fetchNextPage()
                }
            }
        })
    }

    private fun initFab() {
        binding.fabFilter.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToFilterFragment()
            )
        }
    }
}