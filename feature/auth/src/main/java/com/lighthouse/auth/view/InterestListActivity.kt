package com.lighthouse.auth.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.base.adapter.makeAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.intentSerializable
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.ActivityInterestBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.request.UploadInterestVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InterestListActivity : BindingActivity<ActivityInterestBinding>(R.layout.activity_interest) {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var adapter: SimpleListAdapter<UploadInterestVO, InterestListTileBinding>

    private val selectedList = MutableLiveData<HashMap<String, List<String>>>(hashMapOf())
    private val selectedListCode: HashMap<String, List<String>> = hashMapOf()

    private var interestList = mutableListOf<UploadInterestVO>()
    private var interestListCode = mutableListOf<UploadInterestVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSelect()
        initAdapter()
        initBack()
        initApply()
        initChip()
        getInterestList()
    }

    private fun initSelect() {
        val result = intent.intentSerializable("SelectedList", HashMap::class.java)
        if (result != null) {
            selectedList.value = result as HashMap<String, List<String>>
        }
    }

    private fun getInterestList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getInterestList()
                viewModel.result.collect {
                    render(it)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbInterest.setVisible()
                binding.rvInterestList.setGone()
                binding.btnApply.setGone()
            }

            is UiState.Success<*> -> {
                binding.rvInterestList.setVisible()
                binding.btnApply.setVisible()
                Log.d("TESTING", uiState.data.toString())
                updateInterestList(uiState.data as List<InterestVO>)
                adapter.submitList(interestList)
                binding.pbInterest.setGone()
            }

            is UiState.Error<*> -> {
                handleException(uiState)
                binding.pbInterest.setGone()
            }
        }
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

    private fun initAdapter() {
        adapter = makeAdapter(
            checkable = true,
            hide = true,
            selectedList.value ?: hashMapOf()
        ) { checkedList: List<Int>, pos: Int ->
            updateChip(checkedList, pos)
        }
        val linearLayoutManager = ScrollSpeedLinearLayoutManager(applicationContext, 8f)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvInterestList.layoutManager = linearLayoutManager
        binding.rvInterestList.adapter = adapter
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initChip() {
        selectedList.observe(this) {
            binding.chipResult.removeAllViews()
            val interests = selectedList.value?.values?.flatten()
            val inflater = LayoutInflater.from(applicationContext)
            interests?.forEach {
                val chip = inflater.inflate(
                    com.lighthouse.android.common_ui.R.layout.home_chip, binding.chipResult, false
                ) as Chip
                chip.text = it
                chip.isCloseIconVisible = false
                binding.chipResult.addView(chip)
            }
        }
    }

    private fun initApply() {
        binding.btnApply.setOnClickListener {
            val intent = Intent()
            intent.putExtra("InterestList", selectedList.value)
            intent.putExtra("InterestListCode", selectedListCode)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun updateChip(checkedId: List<Int>, position: Int) {
        val result = checkedId.map {
            findViewById<Chip>(it).text.toString()
        }.toList()

        val category = interestList[position].category
        val newMap = selectedList.value?.toMutableMap() ?: mutableMapOf()
        newMap[category] = result
        val convert = HashMap(newMap)
        selectedList.postValue(convert)

        val code = interestListCode[position].category
        val tmp = mutableListOf<String>()
        result.forEach {
            val index = interestList[position].interests.indexOf(it)
            if (index != -1) {
                tmp.add(interestListCode[position].interests[index])
            }
        }
        selectedListCode[code] = tmp
    }
}

