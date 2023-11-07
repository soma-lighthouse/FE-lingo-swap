package com.lighthouse.android.home.adapter

import android.content.Context
import com.lighthouse.android.common_ui.BR
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.UserInfoTileBinding
import com.lighthouse.domain.entity.response.vo.ProfileVO

fun makeAdapter(
    context: Context,
    navigateToProfile: (userId: String, name: String, region: String) -> Unit
) =
    SimpleListAdapter<ProfileVO, UserInfoTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.id == new.id }),
        layoutId = R.layout.user_info_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding

            viewHolder.itemView.setOnClickListener {
                navigateToProfile(item.id, item.name, item.region.code)
            }

            binding.setVariable(BR.item, item)
        },
        ads = true,
        context = context
    )
