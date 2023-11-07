package com.lighthouse.android.chats.uikit.channellist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lighthouse.android.chats.R
import com.lighthouse.android.chats.databinding.ChannelListItemBinding
import com.lighthouse.android.common_ui.util.ImageUtils
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.user.Member
import com.sendbird.uikit.activities.adapter.ChannelListAdapter
import com.sendbird.uikit.activities.viewholder.BaseViewHolder

class CustomChannelListAdapter(
    private val toProfile: (String) -> Unit,
) : ChannelListAdapter() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<GroupChannel> {
        val binding =
            ChannelListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChannelListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<GroupChannel>, position: Int) {
        super.onBindViewHolder(holder, position)

    }

    inner class ChannelListViewHolder(private val binding: ChannelListItemBinding) :
        BaseViewHolder<GroupChannel>(binding.root) {
        override fun bind(item: GroupChannel) {
            val context = itemView.context
            val lastMessage = item.lastMessage

            //set title
            val members = item.members
            val opponent: Member
            if (members.size > 1) {
                val currentUser = SendbirdChat.currentUser?.nickname ?: " "
                opponent = if (members[0].nickname != currentUser) members[0] else members[1]
                binding.tvName.text = opponent.nickname
                // channel cover image
                ImageUtils.newInstance()
                    .setImage(binding.ivProfileImg, opponent.profileUrl, binding.root.context)

                binding.ivProfileImg.setOnClickListener {
                    toProfile(opponent.userId)
                }
            } else {
                binding.tvName.text =
                    binding.root.context.getText(com.lighthouse.android.common_ui.R.string.no_user)
            }

            // Alarm setting
            if (item.myPushTriggerOption == GroupChannel.PushTriggerOption.OFF) {
                binding.ivAlarm.setVisible()
            } else {
                binding.ivAlarm.setGone()
            }

            // unread message count
            val unreadCount = item.unreadMessageCount
            binding.tvUnread.text =
                if (unreadCount > 99) context.getString(com.sendbird.uikit.R.string.sb_text_channel_list_unread_count_max) else unreadCount.toString()
            binding.tvUnread.visibility = if (unreadCount > 0) View.VISIBLE else View.GONE
            binding.tvUnread.setBackgroundResource(R.drawable.circle_textview)


            // last message whether user is typing or not
            if (item.isTyping && item.typingUsers.isNotEmpty()) {
                val typingUser = item.typingUsers
                if (typingUser.size == 1) {
                    binding.tvLastMessage.text = String.format(
                        context.getString(com.sendbird.uikit.R.string.sb_text_channel_typing_indicator_single),
                        typingUser[0].nickname
                    )
                }
            } else {
                if (lastMessage != null) {
                    binding.tvLastMessage.text = lastMessage.message
                }
            }

            //last message time
            item.lastMessage?.let {
                val time = it.createdAt
                val currentTime = System.currentTimeMillis()
                val timeDif = kotlin.math.abs(currentTime - time)

                val minutes = timeDif / (1000 * 60)
                val hours = minutes / 60
                val days = hours / 24

                // Determine the appropriate time format to display.
                val timeAgo = when {
                    days > 0 -> "$days days ago"
                    hours > 0 -> "$hours hours ago"
                    minutes > 0 -> "$minutes min ago"
                    else -> "Just now"
                }

                binding.tvLastMessageTime.text = timeAgo
            }

        }

    }
}