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
    private var startTime: Double = 0.0
    private var endTime: Double = 0.0

    @Inject
    lateinit var navigator: Navigator

    override fun onStart() {
        super.onStart()
        startTime = System.currentTimeMillis().toDouble()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_chat)
        observeFirstQuestion()
    }

    override fun navigateToFlow(flow: NavigationFlow) {
        navigator.navigateToFlow(flow)
    }

    private fun observeFirstQuestion() {
        viewModel.sendQuestion.observe(this) {
            if (it != null && endTime == 0.0) {
                endTime = System.currentTimeMillis().toDouble()
                viewModel.sendQuestionInteractLogging(endTime - startTime, it)
            }
        }
    }
}