package com.lighthouse.android.common_ui.server_driven.viewholders.default_holder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.response.server_driven.ContentVO

abstract class DefaultViewHolder(
    private val binding: ViewDataBinding,
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun onBind(data: ContentVO)

    abstract fun getViewType(): ViewType
}