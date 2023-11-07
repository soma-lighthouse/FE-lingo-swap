package com.lighthouse.auth.selection_adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.auth.databinding.CountryTileBinding
import com.lighthouse.auth.viewmodel.AuthViewModel


class CountryViewHolder(private val binding: CountryTileBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(position: Int, listener: AuthViewModel, multiSelection: Boolean) {
        binding.country = listener.country.elementAtOrElse(position) { null }
        binding.viewModel = listener
        binding.position = position
        binding.multi = multiSelection
    }
}