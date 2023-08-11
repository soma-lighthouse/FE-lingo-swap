package com.lighthouse.android.chats

import androidx.fragment.app.viewModels
import com.lighthouse.android.chats.uikit.channellist.CustomChannelList
import com.lighthouse.android.chats.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : CustomChannelList() {
    val viewModel: ChatViewModel by viewModels()

}