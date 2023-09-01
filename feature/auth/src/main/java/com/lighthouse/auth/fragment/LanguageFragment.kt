package com.lighthouse.auth.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.auth.R
import com.lighthouse.auth.adapter.SelectionAdapter
import com.lighthouse.auth.databinding.FragmentLanguageBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.Selection
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BindingFragment<FragmentLanguageBinding>(R.layout.fragment_language),
    SelectionAdapter.OnItemClickListenerLang {
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var adapter: SelectionAdapter
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
            val result = it.getStringArrayListExtra("LanguageNameList")?.first()
            val pos = it.getIntExtra("position", -1)
            if (pos != -1 && result != null) {
                val code = it.getStringArrayListExtra("LanguageCodeList")?.first() ?: "language"
                dataList[pos].name = result
                dataList[pos].code = code
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
        adapter = SelectionAdapter(
            multiSelection = false,
            context = requireContext(),
            type = SelectionAdapter.LEVEL,
            langListener = this
        )
        adapter.submitList(dataList as List<Selection>?)
        val linearLayoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvLanguageLevel.layoutManager = linearLayoutManager
        binding.rvLanguageLevel.adapter = adapter
    }

    override fun countrySelect(position: Int) {
        val intent = mainNavigator.navigateToLanguage(
            requireContext(),
            Pair("selected", dataList.map { it.name }),
            Pair("position", position),
            Pair("multiSelect", false)
        )

        resultLauncher.launch(intent)
    }

    override fun levelSelect(level: Int, position: Int) {
        dataList[position].level = level + 1
    }

    override fun deleteSelect(position: Int) {
        dataList.removeAt(position)
        adapter.notifyItemRemoved(position)
        for (i in position until dataList.size) {
            adapter.notifyItemChanged(i)
        }
    }
}