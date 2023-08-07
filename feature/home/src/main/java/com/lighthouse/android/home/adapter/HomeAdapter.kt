package com.lighthouse.android.home.adapter

import android.content.res.Resources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.lighthouse.android.common_ui.BR
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.constant.Constant
import com.lighthouse.android.common_ui.databinding.LanguageTabBinding
import com.lighthouse.android.common_ui.databinding.UserInfoTileBinding
import com.lighthouse.domain.response.vo.ProfileVO

fun makeAdapter() =
    SimpleListAdapter<ProfileVO, UserInfoTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.id == new.id }),
        layoutId = R.layout.user_info_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding
            Glide.with(binding.ivProfileImg).load(item.profileImage)
                .placeholder(R.drawable.placeholder)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerInside()
                .override(calSize(Constant.PROFILE_IMAGE_SIZE))
                .dontAnimate()
                .into(binding.ivProfileImg)

            val flag = binding.root.context.resources.getIdentifier(
                item.region, "drawable", binding.root.context.packageName
            )

            val adapter =
                SimpleListAdapter<String, LanguageTabBinding>(diffCallBack = ItemDiffCallBack<String>(
                    onContentsTheSame = { old, new -> old == new },
                    onItemsTheSame = { old, new -> old == new }),
                    layoutId = R.layout.language_tab,
                    onBindCallback = { v, s ->
                        val binding = v.binding
                        binding.tvLanguage.text = s
                    })


            val languages = item.languages.map {
                "${it.code}/Lv.${it.level}"
            }

            adapter.submitList(languages)


            binding.rvLanguage.adapter = adapter

            binding.ivFlag.setImageResource(flag)
            binding.ivFlag.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
            binding.ivFlag.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
            binding.ivFlag.requestLayout()

            binding.setVariable(BR.item, item)
        }
    )


private fun calSize(size: Float?): Int {
    val density = Resources.getSystem().displayMetrics.density

    return (size?.times(density) ?: 0).toInt()
}
