package com.lighthouse.android.common_ui.base.selection_adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.databinding.CountryTileBinding
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.domain.entity.response.vo.CountryVO


class CountryViewHolder(private val binding: CountryTileBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: CountryVO) {
        binding.tvCountry.text = item.name

        val flag = binding.root.context.resources.getIdentifier(
            item.code, "drawable", binding.root.context.packageName
        )
        binding.ivFlag.setImageResource(flag)
        binding.ivFlag.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
        binding.ivFlag.requestLayout()

        if (item.select) {
            binding.btnCheck.setVisible()
        } else {
            binding.btnCheck.setGone()
        }
    }
}