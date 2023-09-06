package com.lighthouse.android.chats.uikit.channel

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lighthouse.android.chats.R
import com.lighthouse.android.chats.databinding.CustomChannelHeaderBinding
import com.lighthouse.android.common_ui.util.toast
import com.sendbird.android.SendbirdChat
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.user.Member
import com.sendbird.uikit.modules.components.ChannelHeaderComponent

class CustomChannelHeader : ChannelHeaderComponent() {
    private lateinit var binding: CustomChannelHeaderBinding

    override fun onCreateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        args: Bundle?,
    ): View {
        binding = CustomChannelHeaderBinding.inflate(inflater, parent, false)
        binding.btnBack.setOnClickListener(this::onLeftButtonClicked)
        binding.tbProfile.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_setting -> {
                    onRightButtonClicked(binding.root)
                    true
                }

                R.id.item_alarm -> {
                    context.toast("alarm clicked")
                    true
                }

                else -> {
                    true
                }
            }
        }
        binding.tbProfile.setBackgroundColor(Color.WHITE)


        return binding.root
    }


    override fun notifyChannelChanged(channel: GroupChannel) {
        val members = channel.members
        val opponent: Member
        if (members.size > 1) {
            val currentUser = SendbirdChat.currentUser?.nickname ?: " "
            opponent =
                if (members[0].nickname != currentUser) members[0] else members[1]
            binding.tvOpponentName.text = opponent.nickname
            binding.tvOnline.text = opponent.connectionStatus.toString()
        } else {
            binding.tvOpponentName.text = channel.name
        }

    }
}