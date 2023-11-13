package com.lighthouse.android.chats

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.lighthouse.android.chats.uikit.channellist.CustomChannelList
import com.lighthouse.android.chats.viewmodel.ChatViewModel
import com.lighthouse.android.common_ui.util.getParams
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : CustomChannelList() {
    val viewModel: ChatViewModel by viewModels()

    private val args: ChatFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val channelUrl = args.channelUrl

        if (channelUrl.isNotEmpty()) {
            joinChannel(channelUrl)
        }
        if (args.path.isNotEmpty()) {
            // 어떤 데이터가 필요한지 domain에서 가져오는 것이 좋다. (path, argument를 domain에서 관리한다)
            args.path.getParams().let {
                if (it["channelUrl"].isNullOrEmpty()) return@let
                joinChannel(it["channelUrl"]!!)
            }
        }
    }
}