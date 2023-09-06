package com.lighthouse.android.chats.uikit.channel.viewholder

import android.text.format.DateUtils
import android.view.View
import com.lighthouse.android.chats.databinding.MessageMeBinding
import com.lighthouse.android.chats.uikit.util.drawStatus
import com.sendbird.android.channel.BaseChannel
import com.sendbird.android.message.BaseMessage
import com.sendbird.android.message.Reaction
import com.sendbird.android.message.SendingStatus
import com.sendbird.uikit.activities.viewholder.GroupChannelMessageViewHolder
import com.sendbird.uikit.consts.ClickableViewIdentifier
import com.sendbird.uikit.interfaces.OnItemClickListener
import com.sendbird.uikit.interfaces.OnItemLongClickListener
import com.sendbird.uikit.model.MessageListUIParams
import java.util.concurrent.ConcurrentHashMap


class MessageMeViewHolder(
    private val binding: MessageMeBinding,
) : GroupChannelMessageViewHolder(binding.root) {
    override fun getClickableViewMap(): MutableMap<String, View> {
        val viewMap = ConcurrentHashMap<String, View>()
        viewMap[ClickableViewIdentifier.Chat.name] = binding.tvMessage
        return viewMap
    }

    override fun setEmojiReaction(
        reactionList: MutableList<Reaction>,
        emojiReactionClickListener: OnItemClickListener<String>?,
        emojiReactionLongClickListener: OnItemLongClickListener<String>?,
        moreButtonClickListener: View.OnClickListener?,
    ) {
        // unneeded
    }

    override fun bind(channel: BaseChannel, message: BaseMessage, params: MessageListUIParams) {
        val context = binding.root.context
        val sendingStatus = message.sendingStatus == SendingStatus.SUCCEEDED

        binding.tvSentAt.visibility = if (sendingStatus) View.VISIBLE else View.GONE
        val sentAt =
            DateUtils.formatDateTime(context, message.createdAt, DateUtils.FORMAT_SHOW_TIME)
        binding.tvSentAt.text = sentAt
        binding.tvMessage.text = message.message
        binding.ivStatus.visibility = drawStatus(binding.ivStatus, message, channel)

        val padding = context.resources.getDimensionPixelSize(com.sendbird.uikit.R.dimen.sb_size_8)
        binding.root.setPadding(
            binding.root.paddingLeft,
            padding,
            binding.root.paddingRight,
            padding
        )
    }
}