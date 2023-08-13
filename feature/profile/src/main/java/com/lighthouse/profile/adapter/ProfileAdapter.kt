package com.lighthouse.profile.adapter

import android.view.LayoutInflater
import com.google.android.material.chip.Chip
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.domain.entity.response.vo.InterestVO
import com.lighthouse.profile.R
import com.lighthouse.profile.databinding.InterestTileBinding

fun makeAdapter() =
    SimpleListAdapter<InterestVO, InterestTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.interest == new.interest }
        ),
        layoutId = R.layout.interest_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding
            binding.tvInterestTitle.text = item.category

            val inflator = LayoutInflater.from(binding.root.context)
            item.interest.forEach {
                val chip = inflator.inflate(
                    com.lighthouse.android.common_ui.R.layout.chip,
                    binding.chipInterest,
                    false
                ) as Chip

                chip.text = it
                chip.isCloseIconVisible = false
                chip.isCheckable = false

                binding.chipInterest.addView(chip)
            }

        }
    )