package com.lighthouse.android.chats.uikit.channel

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.lighthouse.android.chats.viewmodel.ChatViewModel
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.Injector
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.android.common_ui.util.toast
import com.lighthouse.navigation.MainNavigator
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.params.UserMessageCreateParams
import com.sendbird.uikit.fragments.ChannelFragment
import com.sendbird.uikit.model.ReadyStatus
import com.sendbird.uikit.modules.ChannelModule
import com.sendbird.uikit.modules.components.MessageInputComponent
import com.sendbird.uikit.modules.components.MessageListComponent
import com.sendbird.uikit.vm.ChannelViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch

open class CustomChannel : ChannelFragment() {
    private val viewModels: ChatViewModel by activityViewModels()
    private val mainNavigator: MainNavigator by lazy {
        EntryPointAccessors.fromActivity(
            requireActivity(),
            Injector.MainNavigatorInjector::class.java
        ).mainNavigator()
    }

    override fun onCreateModule(args: Bundle): ChannelModule {
        val module: ChannelModule = super.onCreateModule(args)

        module.setHeaderComponent(CustomChannelHeader())
        module.setInputComponent(CustomMessageInputComponent())
        module.messageListComponent = CustomMessageList()
        com.sendbird.uikit.R.id.sb_fragment_container
        return module
    }

    override fun onBindMessageListComponent(
        messageListComponent: MessageListComponent,
        viewModel: ChannelViewModel,
        channel: GroupChannel?,
    ) {
        super.onBindMessageListComponent(messageListComponent, viewModel, channel)

        if (messageListComponent is CustomMessageList) {
            messageListComponent.setOnMessageProfileClickListener { view, position, data ->
                context.toast("clicked!")
                mainNavigator.navigateToProfile(
                    requireContext(),
                    Pair("userId", data.sender!!.userId),
                    Pair("isMe", false),
                    Pair("isChat", true)
                )

                onMessageProfileClicked(view, position, data)
            }
        }
    }


    override fun onBindMessageInputComponent(
        inputComponent: MessageInputComponent,
        viewModel: ChannelViewModel,
        channel: GroupChannel?,
    ) {
        super.onBindMessageInputComponent(inputComponent, viewModel, channel)

        if (inputComponent is CustomMessageInputComponent) {
            val customInput = module.messageInputComponent as CustomMessageInputComponent
            customInput.voiceInput = View.OnClickListener {
                takeVoiceRecorder()
            }


            lifecycleScope.launch {
                viewModels.sendQuestion.asFlow()
                    .buffer(1, BufferOverflow.DROP_LATEST)
                    .collect { question ->
                        val params = UserMessageCreateParams(question).apply {
                            customType = StringSet.question_type
                        }
                        channel?.sendUserMessage(params) { _, e ->
                            if (e != null) {
                                // Handle errors here
                            }
                        }
                        delay(Constant.DELAY) // Introduce a delay after sending the message
                    }
            }


            customInput.channel = channel
        }
    }

    override fun onBeforeReady(
        status: ReadyStatus,
        module: ChannelModule,
        viewModel: ChannelViewModel,
    ) {
        super.onBeforeReady(status, module, viewModel)
        val channel = viewModel.channel ?: return

        module.messageListComponent.setAdapter(CustomListAdapter(channel, true) { id, isMe ->
            mainNavigator.navigateToProfile(
                requireContext(),
                Pair("userId", id),
                Pair("isMe", isMe),
                Pair("isChat", true)
            )
        })
    }
}