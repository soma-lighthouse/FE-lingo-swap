package com.lighthouse.android.common_ui.server_driven.viewholders.viewholder

import android.util.Log
import android.view.ViewGroup
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.HomeInfoTileBinding
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.InflateViewType
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.entity.response.server_driven.ContentVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeTitleViewHolder(
    parent: ViewGroup,
    private val binding: HomeInfoTileBinding = InflateViewType.inflateView(
        parent,
        R.layout.home_info_tile
    ),
) : DefaultViewHolder(binding) {
    override fun onBind(data: ContentVO) {
        data as ContentVO.HomeTitleContent

        Log.d("TESTING", data.toString())

        CoroutineScope(Dispatchers.Main).launch {
            binding.tvHomeTitle.text = SpannableStringBuilderProvider.getSpannableBuilder(
                data.tvHomeTitle,
                binding.root.context
            )

        }
    }

    override fun getViewType() = ViewType.HomeTitleViewType
}