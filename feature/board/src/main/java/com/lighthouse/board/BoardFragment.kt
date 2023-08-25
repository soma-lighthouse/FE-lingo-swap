package com.lighthouse.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lighthouse.board.databinding.FragmentBoardBinding
import com.lighthouse.board.view.TabContentFragment
import com.lighthouse.board.viewmodel.BoardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardFragment : Fragment() {
    private val viewModel: BoardViewModel by viewModels()
    private lateinit var binding: FragmentBoardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board, container, false)

        initSpinner()
        initTab()
        initFab()

        return binding.root
    }

    private fun initSpinner() {
        val arrayList = arrayListOf(
            resources.getString(com.lighthouse.android.common_ui.R.string.sort_latest),
            resources.getString(com.lighthouse.android.common_ui.R.string.sort_top_rated)
        )
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            com.lighthouse.android.common_ui.R.layout.spinner_item,
            arrayList
        ).apply {
            setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        }

        binding.spinnerSort.adapter = arrayAdapter
    }

    private fun initTab() {
        binding.vpBoard.adapter = TabViewPagerAdapter(this)
        TabLayoutMediator(binding.tabBoard, binding.vpBoard) { tab, position ->
            tab.text =
                resources.getStringArray(com.lighthouse.android.common_ui.R.array.tab_name)[position]
        }.attach()
    }

    inner class TabViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int =
            resources.getStringArray(com.lighthouse.android.common_ui.R.array.tab_name).size

        override fun createFragment(position: Int): Fragment {
            val fragment = TabContentFragment()

            fragment.arguments = Bundle().apply {
                putInt("tab_pos", position)
                putString("order", binding.spinnerSort.selectedItem.toString())
            }
            return fragment
        }
    }

    private fun initFab() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(BoardFragmentDirections.actionBoardFragmentToAddFragment())
        }
    }

}