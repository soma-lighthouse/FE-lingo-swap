package com.lighthouse.android.common_ui.base.selection_adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.databinding.CountryTileBinding
import com.lighthouse.domain.entity.response.vo.CountryVO


class CountryViewHolder(private val binding: CountryTileBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: CountryVO) {
        binding.country = item
    }
}