package com.lighthouse.auth.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.auth.R
import com.lighthouse.auth.adapter.LanguageAdapter
import com.lighthouse.auth.databinding.FragmentLanguageBinding
import com.lighthouse.domain.entity.response.vo.LanguageVO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BindingFragment<FragmentLanguageBinding>(R.layout.fragment_language) {
    private lateinit var adapter: LanguageAdapter
    private val dataList: MutableList<LanguageVO> =
        mutableListOf(LanguageVO(code = "English", level = 1))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        initNext()
        initAdapter()
        initAdd()
    }

    private fun initAdd() {
        binding.btnAdd.setOnClickListener {
            dataList.add(LanguageVO(code = "English", level = 1))
            adapter.notifyItemInserted(dataList.size - 1)
        }
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initNext() {
        binding.btnNext.setOnClickListener {
            findNavController().navigate(LanguageFragmentDirections.actionLanguageFragmentToCountryFragment())
        }
    }

    private fun initAdapter() {
        adapter = LanguageAdapter(requireContext(), dataList) {
            val intent = mainNavigator.navigateToCountry(requireContext())
            resultLauncher.launch(intent)
        }
        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvLanguageLevel.layoutManager = linearLayoutManager
        binding.rvLanguageLevel.adapter = adapter
    }
}