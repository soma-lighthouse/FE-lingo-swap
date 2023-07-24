package com.lighthouse.android.common_ui.server_driven.viewholders.viewholder

import android.view.ViewGroup
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.UnknownViewTypeBinding
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.InflateViewType
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.response.ContentVO

class UnknownViewHolder(
    private val parent: ViewGroup,
    private val binding: UnknownViewTypeBinding = InflateViewType.inflateView(
        parent,
        R.layout.unknown_view_type
    )
) : DefaultViewHolder(binding) {
    override fun onBind(data: ContentVO) {
        TODO()
    }

    override fun getViewType() = ViewType.UnknownViewType
}