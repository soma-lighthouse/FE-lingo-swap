package com.lighthouse.android.common_ui.server_driven.viewholders.viewholder

import android.content.res.Resources
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lighthouse.android.common_ui.BR
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.constant.Constant
import com.lighthouse.android.common_ui.databinding.UserInfoTileBinding
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.InflateViewType
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.response.dto.ProfileVO
import com.lighthouse.domain.response.server_driven.ContentVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewHolder(
    parent: ViewGroup,
    private val binding: UserInfoTileBinding = InflateViewType.inflateView(
        parent,
        R.layout.user_info_tile
    ),
) : DefaultViewHolder(binding) {
    override fun onBind(data: ContentVO) {
        data as ProfileVO

        CoroutineScope(Dispatchers.Main).launch {
            Glide.with(binding.ivProfileImg)
                .load(data.imageUrl)
                .override(calSize(Constant.PROFILE_IMAGE_SIZE))
                .into(binding.ivProfileImg)

            val flag = binding.root.context.resources.getIdentifier(
                data.region,
                "drawable",
                binding.root.context.packageName
            )

            binding.ivFlag.setImageResource(flag)

            binding.ivFlag.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
            binding.ivFlag.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
            binding.ivFlag.requestLayout()

            binding.setVariable(BR.item, data)
        }

    }

    override fun getViewType(): ViewType = ViewType.UserInfoViewHolder

    private fun calSize(size: Float?): Int {
        val density = Resources.getSystem().displayMetrics.density

        return (size?.times(density) ?: 0).toInt()
    }
}