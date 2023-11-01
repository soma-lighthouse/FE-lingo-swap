package com.lighthouse.auth.selection_adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.auth.databinding.LanguageTileBinding
import com.lighthouse.domain.entity.response.vo.LanguageVO

class LanguageViewHolder(
    private val binding: LanguageTileBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: LanguageVO) {
        binding.tvCountry.text = item.name

        if (item.select) {
            binding.btnCheck.setVisible()
        } else {
            binding.btnCheck.setGone()
        }
    }
}