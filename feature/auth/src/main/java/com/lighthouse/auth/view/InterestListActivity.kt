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
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.auth.R
import com.lighthouse.auth.databinding.ActivityInterestBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.InterestVO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InterestListActivity : BindingActivity<ActivityInterestBinding>(R.layout.activity_interest) {
    private val viewModel: AuthViewModel by viewModels()
    private val checkedList = MutableLiveData<HashMap<String, List<String>>>(hashMapOf())
    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>
    private val selectedList: HashMap<String, List<String>> = hashMapOf()

    private var interestList = listOf<InterestVO>()

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
            for ((key, value) in result) {
                selectedList[key as String] = value as List<String>
            }
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
                interestList = uiState.data as List<InterestVO>
                adapter.submitList(interestList)
                binding.pbInterest.setGone()
            }

            is UiState.Error<*> -> {
                applicationContext.toast(uiState.message.toString())
                binding.pbInterest.setGone()
            }
        }
    }

    private fun initAdapter() {
        adapter = makeAdapter(
            checkable = true,
            hide = true,
            selectedList
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
        checkedList.observe(this) {
            binding.chipResult.removeAllViews()
            val interests = checkedList.value?.values?.flatten()
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
            intent.putExtra("InterestList", checkedList.value)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun updateChip(checkedId: List<Int>, position: Int) {
        val result = checkedId.map {
            findViewById<Chip>(it).text.toString()
        }.toList()

        val category = interestList[position].category
        val newMap = checkedList.value?.toMutableMap() ?: mutableMapOf()
        newMap[category] = result
        val convert = HashMap(newMap)
        checkedList.value = convert
        checkedList.postValue(convert)
    }
}

