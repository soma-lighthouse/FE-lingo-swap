package com.lighthouse.android.common_ui.server_driven.viewholders.viewholder

import android.content.res.Resources
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.UserInfoTileBinding
import com.lighthouse.android.common_ui.server_driven.adapter.HorizontalAdapter
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.InflateViewType
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.response.ContentVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserInfoViewHolder(
    parent: ViewGroup,
    private val binding: UserInfoTileBinding = InflateViewType.inflateView(
        parent,
        R.layout.user_info_tile
    )
) : DefaultViewHolder(binding) {
    override fun onBind(data: ContentVO) {
        data as ContentVO.UserInfoTile

        CoroutineScope(Dispatchers.Main).launch {
            Glide.with(binding.tvProfileImg)
                .load(data.ivProfileImg.image)
                .override(calSize(data.ivProfileImg.width), calSize(data.ivProfileImg.height))
                .into(binding.tvProfileImg)

            binding.tvProfileImg
            binding.tvProfileName.text = SpannableStringBuilderProvider.getSpannableBuilder(
                data.tvProfileName,
                binding.root.context
            )
            binding.tvProfileIntro.text = SpannableStringBuilderProvider.getSpannableBuilder(
                data.tvProfileIntro,
                binding.root.context
            )
            val adapter = HorizontalAdapter()
            binding.rvLanguage.adapter = adapter
            adapter.submitList(data.rvLanguage)

            val flag = binding.root.context.resources.getIdentifier(
                data.ivProfileNation.image,
                "drawable",
                binding.root.context.packageName
            )


            binding.ivFlag.setImageResource(flag)

            binding.ivFlag.layoutParams.width = calSize(data.ivProfileNation.width)
            binding.ivFlag.layoutParams.height = calSize(data.ivProfileNation.height)
            binding.ivFlag.requestLayout()


        }
    }

    override fun getViewType() = ViewType.UserInfoViewType

    private fun calSize(size: Float?): Int {
        val density = Resources.getSystem().displayMetrics.density

        return (size?.times(density) ?: 0).toInt()
    }
}