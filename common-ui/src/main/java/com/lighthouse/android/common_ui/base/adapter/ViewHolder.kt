package com.lighthouse.android.common_ui.base.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.BR

class ViewHolder<B : ViewDataBinding>(val binding: B) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: Any) {
        binding.setVariable(BR.item, item)
        binding.executePendingBindings()
    }
}