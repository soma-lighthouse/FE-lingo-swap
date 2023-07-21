package com.lighthouse.android.common_ui.server_driven.viewholders

import android.view.ViewGroup
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.ChatinfoViewTypeBinding
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.android.common_ui.server_driven.viewholders.util.InflateViewType
import com.lighthouse.domain.constriant.ViewType
import com.lighthouse.domain.response.ContentVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatRoomInfoViewHolder(
    private val parent: ViewGroup,
    private val binding: ChatinfoViewTypeBinding = InflateViewType.inflateView(
        parent,
        R.layout.chatinfo_view_type
    )
) : DefaultViewHolder(binding) {
    override fun onBind(data: ContentVO) {
        data as ContentVO.ChatRoomContent
        CoroutineScope(Dispatchers.Main).launch {
            binding.tvUsername.text = SpannableStringBuilderProvider.getSpannableBuilder(
                data.userName,
                binding.root.context
            )
            binding.tvNation.text = data.nation
            binding.tvAge.text = data.userAge.toString()
            binding.tvLastMessage.text = SpannableStringBuilderProvider.getSpannableBuilder(
                data.lastMessage,
                binding.root.context
            )
        }
    }

    override fun getViewType() = ViewType.ChatRoomInfoViewType
}