package com.lighthouse.android.chats.uikit.channel.viewholder

import android.text.TextUtils
import android.text.format.DateUtils
import android.view.View
import com.lighthouse.android.chats.databinding.MessageOtherBinding
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
import com.lighthouse.android.common_ui.util.ImageUtils as Image

class MessageOtherViewHolder(
    private val binding: MessageOtherBinding,
    private val toProfile: (String, Boolean) -> Unit,
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
        // TODO("Not yet implemented")
    }

    override fun bind(channel: BaseChannel, message: BaseMessage, params: MessageListUIParams) {
        val context = binding.root.context
        val sendState = message.sendingStatus == SendingStatus.SUCCEEDED
        binding.tvSentAt.visibility = if (sendState) View.VISIBLE else View.GONE
        val sentAt =
            DateUtils.formatDateTime(context, message.createdAt, DateUtils.FORMAT_SHOW_TIME)
        binding.tvSentAt.text = sentAt

        val sender = message.sender
        val nickname = if (sender == null || TextUtils.isEmpty(sender.nickname)) context.getString(
            com.sendbird.uikit.R.string.sb_text_channel_list_title_unknown
        ) else sender.nickname

        binding.tvNickname.text = nickname

        var url = ""
        if (sender != null && !TextUtils.isEmpty(sender.profileUrl)) {
            url = sender.profileUrl
        }

        Image.newInstance().setImage(binding.ivProfileView, url, binding.root.context)

        binding.ivProfileView.setOnClickListener {
            val id = sender?.userId ?: ""
            toProfile(id, false)
        }

        binding.tvMessage.text = message.message

    }
}