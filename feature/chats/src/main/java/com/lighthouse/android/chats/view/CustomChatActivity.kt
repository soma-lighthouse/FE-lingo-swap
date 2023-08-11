package com.lighthouse.android.chats.view

import android.os.Bundle
import androidx.activity.viewModels
import com.lighthouse.android.chats.R
import com.lighthouse.android.chats.viewmodel.ChatViewModel
import com.lighthouse.navigation.NavigationFlow
import com.lighthouse.navigation.Navigator
import com.lighthouse.navigation.ToFlowNavigatable
import com.sendbird.uikit.activities.ChannelActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomChatActivity @Inject constructor() : ChannelActivity(), ToFlowNavigatable {
    private val viewModel: ChatViewModel by viewModels()

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_chat)
    }

    override fun navigateToFlow(flow: NavigationFlow) {
        navigator.navigateToFlow(flow)
    }
}