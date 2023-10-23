package com.lighthouse.android.common_ui.server_driven.viewholders.viewholder

import android.view.ViewGroup
import com.lighthouse.android.common_ui.BR
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.UserInfoTileBinding
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.InflateViewType
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.entity.response.server_driven.ContentVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
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
            val util = ImageUtils.newInstance()
            util.setFlagImage(binding.ivFlag, data.region.code, binding.root.context)
            util.setImage(binding.ivProfileImg, data.profileImageUri, binding.root.context)

            binding.setVariable(BR.item, data)
        }

    }

    override fun getViewType(): ViewType = ViewType.UserInfoViewHolder
}