package com.lighthouse.android.chats.uikit.channel

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lighthouse.android.chats.databinding.MessageMeBinding
import com.lighthouse.android.chats.databinding.MessageOtherBinding
import com.lighthouse.android.chats.databinding.QuestionMeBinding
import com.lighthouse.android.chats.databinding.QuestionOtherBinding
import com.lighthouse.android.chats.uikit.channel.viewholder.MessageMeViewHolder
import com.lighthouse.android.chats.uikit.channel.viewholder.MessageOtherViewHolder
import com.lighthouse.android.chats.uikit.channel.viewholder.QuestionMessageMeViewHolder
import com.lighthouse.android.chats.uikit.channel.viewholder.QuestionMessageOtherViewHolder
import com.lighthouse.android.common_ui.util.StringSet
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.message.UserMessage
import com.sendbird.uikit.activities.adapter.MessageListAdapter
import com.sendbird.uikit.activities.viewholder.MessageType
import com.sendbird.uikit.activities.viewholder.MessageViewHolder
import com.sendbird.uikit.utils.MessageUtils

class CustomListAdapter(
    channel: GroupChannel,
    userMessageGroupUI: Boolean,
    private val toProfile: (String, Boolean) -> Unit,
) : MessageListAdapter(channel, userMessageGroupUI) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            VIEW_QUESTION_ME -> {
                val meBinding = QuestionMeBinding.inflate(inflater, parent, false)
                val viewHolder = QuestionMessageMeViewHolder(meBinding)

                return applyClickListener(viewHolder)
            }

            VIEW_QUESTION_OTHER -> {
                val otherBinding = QuestionOtherBinding.inflate(inflater, parent, false)
                val viewHolder = QuestionMessageOtherViewHolder(otherBinding, toProfile)

                return applyClickListener(viewHolder)
            }

            MessageType.VIEW_TYPE_USER_MESSAGE_OTHER.value -> {
                val bind = MessageOtherBinding.inflate(inflater, parent, false)
                val viewHolder = MessageOtherViewHolder(bind, toProfile)
                return applyClickListener(viewHolder)
            }

            MessageType.VIEW_TYPE_USER_MESSAGE_ME.value -> {
                val bind = MessageMeBinding.inflate(inflater, parent, false)
                val viewHolder = MessageMeViewHolder(bind)
                return applyClickListener(viewHolder)
            }
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

    private fun applyClickListener(viewHolder: MessageViewHolder): MessageViewHolder {
        viewHolder.setMessageUIConfig(messageUIConfig)

        val views: Map<String, View> = viewHolder.clickableViewMap
        for (entry in views.entries) {
            val identifier = entry.key
            entry.value.setOnClickListener {
                val msgPosition = viewHolder.adapterPosition
                if (msgPosition != -1) {
                    onListItemClickListener?.onIdentifiableItemClick(
                        it,
                        identifier,
                        msgPosition,
                        getItem(msgPosition)
                    )
                }
            }

            entry.value.setOnLongClickListener {
                val msgPosition = viewHolder.adapterPosition
                if (msgPosition != -1) {
                    onListItemLongClickListener?.onIdentifiableItemLongClick(
                        it,
                        identifier,
                        msgPosition,
                        getItem(msgPosition)
                    )
                }
                true
            }
        }
        return viewHolder
    }

    companion object {
        const val VIEW_QUESTION_ME = 1001
        const val VIEW_QUESTION_OTHER = 1002
    }
}