package com.lighthouse.android.common_ui.server_driven.viewholders

import android.view.ViewGroup
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.TitleViewTypeBinding
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.InflateViewType
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.response.ContentVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TitleViewHolder(
    parent: ViewGroup,
    private val binding: TitleViewTypeBinding = InflateViewType.inflateView(
        parent,
        R.layout.title_view_type
    )
) : DefaultViewHolder(binding) {
    override fun onBind(data: ContentVO) {
        data as ContentVO.TitleContent
        CoroutineScope(Dispatchers.Main).launch {
            binding.tvTitle.text =
                SpannableStringBuilderProvider.getSpannableBuilder(
                    data.title,
                    binding.root.context
                )
            binding.tvDetail.text = SpannableStringBuilderProvider.getSpannableBuilder(
                data.detail,
                binding.root.context
            )
        }
    }

    override fun getViewType() = ViewType.TitleViewType
}