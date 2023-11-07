package com.lighthouse.android.common_ui.server_driven.viewholders.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.getViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.ItemDiffCallback
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.entity.response.server_driven.ViewTypeVO

class DrivenAdapter : ListAdapter<ViewTypeVO, DefaultViewHolder>(
    ItemDiffCallback<ViewTypeVO>(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old.id == new.id }
    )
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder {
        return getViewHolder(parent, ViewType.getViewTypeByOrdinal(viewType))
    }

    override fun onBindViewHolder(holder: DefaultViewHolder, position: Int) {
        holder.onBind(getItem(position).content)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType.ordinal
    }
}