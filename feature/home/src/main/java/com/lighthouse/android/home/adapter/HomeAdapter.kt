package com.lighthouse.android.home.adapter

import android.content.Context
import android.util.Log
import com.lighthouse.android.common_ui.BR
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.LanguageTabBinding
import com.lighthouse.android.common_ui.databinding.UserInfoTileBinding
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.domain.entity.response.vo.ProfileVO

fun makeAdapter(
    context: Context,
    navigateToProfile: (userId: String) -> Unit
) =
    SimpleListAdapter<ProfileVO, UserInfoTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.id == new.id }),
        layoutId = R.layout.user_info_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding
            val util = ImageUtils.newInstance()

            val adapter =
                SimpleListAdapter<String, LanguageTabBinding>(diffCallBack = ItemDiffCallBack(
                    onContentsTheSame = { old, new -> old == new },
                    onItemsTheSame = { old, new -> old == new }),
                    layoutId = R.layout.language_tab,
                    onBindCallback = { v, s ->
                        val binding = v.binding
                        binding.tvLanguage.text = s
                    })


            val languages = item.languages.map {
                "${it.name}/Lv.${it.level}"
            }

            Log.d("TESTING", item.toString())

            viewHolder.itemView.setOnClickListener {
                navigateToProfile(item.id)
            }

            util.setFlagImage(binding.ivFlag, item.region.code, context)
            util.setImage(binding.ivProfileImg, item.profileImageUri, context)
            adapter.submitList(languages)

            binding.rvLanguage.adapter = adapter

            binding.setVariable(BR.item, item)
        },
        ads = true,
        context = context
    )
