package com.lighthouse.auth.view

import android.os.Bundle
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
        mutableListOf(LanguageVO(name = "English", level = 1, code = "en"))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        initNext()
        initAdapter()
        initAdd()
        observeResult()
    }

    private fun observeResult() {
        getResult.observe(viewLifecycleOwner) {
            val result =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    it.getSerializableExtra("Language", LanguageVO::class.java)
                } else {
                    it.getSerializableExtra("Language") as LanguageVO
                }
            val pos = it.getIntExtra("position", -1)
            if (pos != -1 && result != null) {
                dataList[pos].name = result.name
                dataList[pos].code = result.code
                adapter.notifyItemChanged(pos)
            } else {
                context.toast(pos.toString())
            }
        }
    }

    private fun initAdd() {
        binding.btnAdd.setOnClickListener {
            if (dataList.size < 5) {
                dataList.add(LanguageVO(name = "country", level = 1, code = ""))
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
            removeExtra()
            viewModel.registerInfo.languages = dataList.map {
                mapOf("code" to it.code, "level" to it.level)
            }
            findNavController().navigate(LanguageFragmentDirections.actionLanguageFragmentToCountryFragment())
        }
    }

    private fun removeExtra() {
        dataList.forEach {
            if (it.name == "country") {
                dataList.remove(it)
            }
        }
    }

    private fun initAdapter() {
        adapter = LanguageLevelAdapter(requireContext(), dataList, { pos ->
            val intent = mainNavigator.navigateToLanguage(
                requireContext(), Pair("selected", dataList.map { it.name }), Pair("position", pos)
            )
            resultLauncher.launch(intent)
        }, { level, position ->
            dataList[position].level = level + 1
        })

        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvLanguageLevel.layoutManager = linearLayoutManager
        binding.rvLanguageLevel.adapter = adapter
    }
}