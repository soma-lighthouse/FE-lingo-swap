package com.lighthouse.auth.selection_adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.auth.databinding.CountryTileBinding
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.CountryVO


class CountryViewHolder(private val binding: CountryTileBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(c: CountryVO, position: Int, listener: AuthViewModel, multiSelection: Boolean) {
        binding.country = c
        binding.viewModel = listener
        binding.position = position
        binding.multi = multiSelection
    }
}