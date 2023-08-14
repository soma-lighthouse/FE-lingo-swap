package com.lighthouse.auth.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.auth.R
import com.lighthouse.auth.adapter.LanguageLevelAdapter
import com.lighthouse.auth.databinding.FragmentLanguageBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.LanguageVO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BindingFragment<FragmentLanguageBinding>(R.layout.fragment_language) {
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var adapter: LanguageLevelAdapter
    private val dataList: MutableList<LanguageVO> =
        mutableListOf(LanguageVO(name = "English", level = 1))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TESTING", "${viewModel.registerInfo}")
        initBack()
        initNext()
        initAdapter()
        initAdd()
    }

    private fun initAdd() {
        binding.btnAdd.setOnClickListener {
            if (dataList.size < 5) {
                dataList.add(LanguageVO(name = "English", level = 1))
                adapter.notifyItemInserted(dataList.size - 1)
            } else {
                context.toast("Only up to maximum 5 Language")
            }
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
        adapter = LanguageLevelAdapter(requireContext(), dataList, { pos ->
            val intent = mainNavigator.navigateToLanguage(requireContext())
            resultLauncher.launch(intent)
            getResult.observe(viewLifecycleOwner) {
                val result = it.getStringExtra("Language").toString()
                dataList[pos].name = result
                adapter.notifyItemChanged(pos)
            }
        }, { level, position ->
            dataList[position].level = level + 1
        })

        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvLanguageLevel.layoutManager = linearLayoutManager
        binding.rvLanguageLevel.adapter = adapter
    }
}