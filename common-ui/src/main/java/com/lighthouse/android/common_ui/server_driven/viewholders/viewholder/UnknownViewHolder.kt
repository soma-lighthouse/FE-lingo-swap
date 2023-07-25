package com.lighthouse.android.common_ui.server_driven.viewholders.viewholder

import android.view.ViewGroup
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.UnknownTileBinding
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.InflateViewType
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.response.ContentVO

class UnknownViewHolder(
    private val parent: ViewGroup,
    private val binding: UnknownTileBinding = InflateViewType.inflateView(
        parent,
        R.layout.unknown_tile
    )
) : DefaultViewHolder(binding) {
    override fun onBind(data: ContentVO) {
        binding.textView.text = "Unknown textView"
    }

    override fun getViewType() = ViewType.UnknownViewType
}