package com.lighthouse.android.chats.uikit.channel

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lighthouse.android.chats.databinding.QuestionMeBinding
import com.lighthouse.android.chats.databinding.QuestionOtherBinding
import com.lighthouse.android.chats.uikit.channel.viewholder.QuestionMessageMeViewHolder
import com.lighthouse.android.chats.uikit.channel.viewholder.QuestionMessageOtherViewHolder
import com.lighthouse.android.common_ui.util.StringSet
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.message.UserMessage
import com.sendbird.uikit.activities.adapter.MessageListAdapter
import com.sendbird.uikit.activities.viewholder.MessageViewHolder
import com.sendbird.uikit.utils.MessageUtils

class CustomListAdapter(
    channel: GroupChannel,
    userMessageGroupUI: Boolean,
    private val toProfile: (Int, Boolean) -> Unit,
) : MessageListAdapter(channel, userMessageGroupUI) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == VIEW_QUESTION_ME) {
            return QuestionMessageMeViewHolder(QuestionMeBinding.inflate(inflater, parent, false))
        } else if (viewType == VIEW_QUESTION_OTHER) {
            val otherBinding = QuestionOtherBinding.inflate(inflater, parent, false)
            return QuestionMessageOtherViewHolder(otherBinding, toProfile)
        }

        // custom viewHolder here
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        val customType = message.customType

        if (!TextUtils.isEmpty(customType) &&
            customType == StringSet.question_type &&
            message is UserMessage
        ) {
            return when {
                MessageUtils.isMine(message) -> VIEW_QUESTION_ME
                else -> VIEW_QUESTION_OTHER
            }

        }

        return super.getItemViewType(position)
    }

    companion object {
        const val VIEW_QUESTION_ME = 1001
        const val VIEW_QUESTION_OTHER = 1002
    }
}