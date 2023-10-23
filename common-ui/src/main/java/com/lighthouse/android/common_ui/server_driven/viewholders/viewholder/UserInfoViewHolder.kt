package com.lighthouse.android.common_ui.server_driven.viewholders.viewholder

import android.view.ViewGroup
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.UserInfoTileBinding
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.server_driven.viewholders.adapter.HorizontalAdapter
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.InflateViewType
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.entity.response.server_driven.ContentVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserInfoViewHolder(
    parent: ViewGroup,
    private val binding: UserInfoTileBinding = InflateViewType.inflateView(
        parent,
        R.layout.user_info_tile
    ),
) : DefaultViewHolder(binding) {
    override fun onBind(data: ContentVO) {
        data as ContentVO.UserInfoTile

        CoroutineScope(Dispatchers.Main).launch {
            val util = ImageUtils.newInstance()

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

            util.setFlagImage(binding.ivFlag, data.ivProfileNation.image, binding.root.context)
            util.setImage(binding.ivProfileImg, data.ivProfileImg.image, binding.root.context)

        }
    }

    override fun getViewType() = ViewType.UserInfoViewType
}