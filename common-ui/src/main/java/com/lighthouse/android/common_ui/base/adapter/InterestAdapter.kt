package com.lighthouse.android.common_ui.base.adapter

import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.listener.InterestListener
import com.lighthouse.domain.entity.response.vo.InterestVO

fun makeInterestAdapter(listener: InterestListener, highLight: Boolean) =
    SimpleListAdapter<InterestVO, InterestListTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onItemsTheSame = { old, new -> old.category == new.category },
            onContentsTheSame = { old, new -> old == new }),
        layoutId = com.lighthouse.android.common_ui.R.layout.interest_list_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding
            binding.item = item
            binding.position = viewHolder.absoluteAdapterPosition
            binding.listener = listener
            binding.highLight = highLight
        }
    )