package com.lighthouse.profile.view

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.common_ui.base.BindingActivity
import com.lighthouse.android.common_ui.base.adapter.ScrollSpeedLinearLayoutManager
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.base.adapter.makeAdapter
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.UiState
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import com.lighthouse.profile.R
import com.lighthouse.profile.databinding.ActivityDetailProfileBinding
import com.lighthouse.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailProfileActivity :
    BindingActivity<ActivityDetailProfileBinding>(R.layout.activity_detail_profile) {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var adapter: SimpleListAdapter<InterestVO, InterestListTileBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBack()
        initFold()
        initMenu()
        initAdapter()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                intent.getStringExtra("userId")?.let {
                    viewModel.getProfileDetail(it).collect {
                        render(it)
                    }
                }
            }
        }
    }

    private fun initBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initMenu() {
        binding.tbProfile.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_edit -> {
                    applicationContext.toast(it.itemId.toString())
                    true
                }

                else -> {
                    true
                }
            }
        }
    }

    private fun initFold() {
        binding.btnInterest.setOnClickListener { _ ->
            if (binding.cvInterest.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.collapseInterest, AutoTransition())
                binding.cvInterest.visibility = View.GONE
                binding.btnInterest.animate().rotation(0f).start()
            } else {
                TransitionManager.beginDelayedTransition(binding.collapseInterest, AutoTransition())
                binding.cvInterest.visibility = View.VISIBLE
                binding.btnInterest.animate().rotation(180f).start()
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.pbDetailLoading.setVisible()
                binding.group1.setGone()
                binding.bottomRectangle.setGone()
                binding.btnSend.setGone()
            }

            is UiState.Success<*> -> {
                binding.group1.setVisible()
                binding.bottomRectangle.setVisible()
                binding.btnSend.setVisible()
                val result = uiState.data as ProfileVO
                initView(result)
                initChip(binding.chipCountry, result.countries)
                initChip(binding.chipLanguage, result.languages.flatMap {
                    listOf("${it.name}/LV${it.level}")
                })
                adapter.submitList(result.interests)
                binding.pbDetailLoading.setGone()
            }

            is UiState.Error<*> -> {
                applicationContext.toast(uiState.message.toString())
                binding.pbDetailLoading.setGone()
            }
        }
    }

    private fun initChip(chipGroup: ChipGroup, contentList: List<String>) {
        val inflater = LayoutInflater.from(applicationContext)
        contentList.forEach {
            val chip = inflater.inflate(
                com.lighthouse.android.common_ui.R.layout.home_chip,
                chipGroup,
                false
            ) as Chip

            chip.text = it
            chip.isCloseIconVisible = false
            chipGroup.addView(chip)
        }
    }

    private fun initAdapter() {
        adapter = makeAdapter()
        val layoutManager = ScrollSpeedLinearLayoutManager(applicationContext, 8f)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvInterest.layoutManager = layoutManager
        binding.rvInterest.adapter = adapter
    }

    private fun initView(profile: ProfileVO) {
        binding.tvNameTitle.text = profile.name
        binding.tvProfileName.text = profile.name
        binding.tvNation.text = profile.region
        binding.tvDescription.text = profile.description

        Glide.with(binding.ivProfileImg)
            .load(profile.profileImageUri)
            .placeholder(com.lighthouse.android.common_ui.R.drawable.placeholder)
            .override(calSize(200f))
            .into(binding.ivProfileImg)

        val flag = binding.root.context.resources.getIdentifier(
            profile.region, "drawable", binding.root.context.packageName
        )
        binding.ivFlag.setImageResource(flag)
        binding.ivFlag.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.requestLayout()
    }
}