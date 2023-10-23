package com.lighthouse.android.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.common_ui.base.BindingFragment
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.base.adapter.makeAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.intentSerializable
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.android.home.R
import com.lighthouse.android.home.databinding.FragmentFilterBinding
import com.lighthouse.android.home.viewmodel.HomeViewModel
import com.lighthouse.domain.entity.request.UploadFilterVO
import com.lighthouse.domain.entity.request.UploadInterestVO
import com.lighthouse.domain.entity.response.FilterVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.navigation.DeepLinkDestination
import com.lighthouse.navigation.deepLinkNavigateTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterFragment : BindingFragment<FragmentFilterBinding>(R.layout.fragment_filter) {
    private val viewModel: HomeViewModel by activityViewModels()

    private var selectedCountryName = listOf<String>()
    private var selectedCountryCode = listOf<String>()
    private var selectedLanguages = listOf<LanguageVO>()
    private var interestList = mutableListOf<UploadInterestVO>()
    private var interestListCode = mutableListOf<UploadInterestVO>()

    private var first = false

    private lateinit var adapter: SimpleListAdapter<UploadInterestVO, InterestListTileBinding>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCountryResult()
        observerInterestResult()
        initBack()
        initCountry()
        initLanguage()
        initInterest()
        initAdapter()
        getFilterFromServer()
        initApply()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        updateChip()
    }

    private fun initApply() {
        observeApply()
        binding.btnApply.setOnClickListener {
            if (checkFilter()) {
                viewModel.uploadFilterSetting(
                    UploadFilterVO(
                        selectedCountryCode,
                        selectedLanguages.map {
                            mapOf("code" to it.code, "level" to it.level)
                        },
                        interestListCode
                    )
                )
            } else {
                requireContext().toast(getString(com.lighthouse.android.common_ui.R.string.filter_error))
            }
        }
    }

    private fun observeApply() {
        viewModel.resetUploadState()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.upload.collect {
                    Log.d("TESTING FILTER", "$it")
                    if (it) {
                        requireContext().toast(getString(com.lighthouse.android.common_ui.R.string.upload_success))
                        viewModel.saveUserProfiles(emptyList())
                        viewModel.next = null
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun checkFilter(): Boolean {
        return selectedCountryName.isNotEmpty() && selectedLanguages.isNotEmpty() && interestList.isNotEmpty()
    }

    private fun getFilterFromServer() {
        viewModel.getFilterFromServer()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filter.collect {
                    if (!first) {
                        render(it)
                    }
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbFilter.setVisible()
                binding.filterGroup.setGone()
                binding.btnApply.setGone()
            }

            is UiState.Success<*> -> {
                if (uiState.data is FilterVO) {
                    binding.filterGroup.setVisible()
                    val data = uiState.data as FilterVO
                    selectedCountryName = data.countries.map { it.name }
                    selectedCountryCode = data.countries.map { it.code }
                    selectedLanguages = data.languages
                    updateInterestList(data.interests)
                    updateChip()
                    first = true
                }
                binding.pbFilter.setGone()
                binding.btnApply.setVisible()
            }

            is UiState.Error<*> -> {
                handleException(uiState)
            }
        }
    }

    private fun updateChip() {
        addChipToGroup(
            binding.chipPreferCountry,
            selectedCountryName
        )
        addChipToGroup(
            binding.chipPreferLanguage,
            selectedLanguages.map { "${it.name}/LV.${it.level}" }
        )

        adapter.submitList(interestList)

    }

    private fun updateInterestList(interest: List<InterestVO>) {
        interest.forEach {
            interestListCode.add(
                UploadInterestVO(
                    it.category.code,
                    it.interests.map { interest -> interest.code })
            )
            interestList.add(
                UploadInterestVO(
                    it.category.name,
                    it.interests.map { interest -> interest.name })
            )
        }
    }

    override fun onResume() {
        super.onResume()
        observeLanguageResult()
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initCountry() {
        binding.clickCountry.setOnClickListener {
            val intent =
                mainNavigator.navigateToCountry(
                    requireContext(),
                    Pair("multiSelect", true),
                    Pair("SelectedList", selectedCountryName)
                )
            resultLauncher.launch(intent)
        }
    }

    private fun initLanguage() {
        binding.clickLanguage.setOnClickListener {
            viewModel.saveLanguageFilter(selectedLanguages)
            findNavController().deepLinkNavigateTo(
                DeepLinkDestination.FromFilterToLanguageLevel(
                    Constant.FILTER
                )
            )
        }
    }

    private fun initInterest() {
        binding.clickInterest.setOnClickListener {
            val hash = hashMapOf<String, List<String>>()

            interestList.forEach {
                hash[it.category] = it.interests
            }

            val intent = mainNavigator.navigateToInterest(
                requireContext(),
                Pair("SelectedList", hash),
            )
            resultLauncher.launch(intent)
        }
    }

    private fun observeCountryResult() {
        getResult.observe(viewLifecycleOwner) {
            val result =
                it.getStringArrayListExtra("CountryNameList")?.toMutableList() ?: mutableListOf()
            Log.d("TESTING LANG", result.toString())
            if (result.isNotEmpty()) {
                selectedCountryName = result
                selectedCountryCode = it.getStringArrayListExtra("CountryCodeList")?.toMutableList()
                    ?: mutableListOf()
                addChipToGroup(binding.chipPreferCountry, selectedCountryName)
            }
        }
    }

    private fun observeLanguageResult() {
        selectedLanguages = viewModel.getLanguageFilter()
        addChipToGroup(
            binding.chipPreferLanguage,
            selectedLanguages.map { "${it.name}/LV.${it.level}" })
    }

    private fun observerInterestResult() {
        getResult.observe(viewLifecycleOwner) {
            val result = it.intentSerializable("InterestList", HashMap::class.java)
            if (result != null) {
                val tmp = mutableListOf<UploadInterestVO>()
                for ((key, value) in result) {
                    value as List<String>
                    if (value.isNotEmpty()) {
                        tmp.add(UploadInterestVO(key as String, value))
                    }
                }
                interestList = tmp
                updateInterestCode(it.intentSerializable("InterestListCode", HashMap::class.java))
            }
            adapter.submitList(interestList)
        }
    }

    private fun updateInterestCode(codes: HashMap<*, *>?) {
        if (codes != null) {
            interestListCode = mutableListOf()
            for ((key, value) in codes) {
                value as List<String>
                if (value.isNotEmpty()) {
                    interestListCode.add(UploadInterestVO(key as String, value))
                }
            }
        }
    }

    private fun addChipToGroup(chipGroup: ChipGroup, contentList: List<String>) {
        chipGroup.removeAllViews()
        val inflater = LayoutInflater.from(context)
        contentList.forEach { content ->
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.home_chip, chipGroup, false
            ) as Chip

            chip.text = content
            chip.isCloseIconVisible = false

            chipGroup.addView(chip)
        }
    }

    private fun initAdapter() {
        adapter = makeAdapter()
        val layoutManager = ScrollSpeedLinearLayoutManager(requireContext(), 8f)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvInterest.layoutManager = layoutManager
        binding.rvInterest.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycleScope.cancel()
    }
}