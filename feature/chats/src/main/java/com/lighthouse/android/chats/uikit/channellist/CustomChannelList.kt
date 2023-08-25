package com.lighthouse.android.chats.uikit.channellist

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lighthouse.android.chats.view.CustomChatActivity
import com.sendbird.android.channel.GroupChannel
import com.sendbird.uikit.activities.ChannelActivity
import com.sendbird.uikit.activities.MessageSearchActivity
import com.sendbird.uikit.fragments.ChannelListFragment
import com.sendbird.uikit.model.ReadyStatus
import com.sendbird.uikit.modules.ChannelListModule
import com.sendbird.uikit.modules.components.HeaderComponent
import com.sendbird.uikit.vm.ChannelListViewModel

open class CustomChannelList : ChannelListFragment() {
    override fun onCreateModule(args: Bundle): ChannelListModule {
        val module: ChannelListModule = super.onCreateModule(args)

        module.setHeaderComponent(CustomHeaderComponent())
        return module
    }

    override fun onBeforeReady(
        status: ReadyStatus,
        module: ChannelListModule,
        viewModel: ChannelListViewModel,
    ) {
        super.onBeforeReady(status, module, viewModel)
        module.channelListComponent.setAdapter(CustomChannelListAdapter())
        module.channelListComponent.setOnItemClickListener { _, _, channel ->
            GroupChannel.getChannel(channel.url) { _, e ->
                if (e != null) {
                    // TODO("error handling")
                } else {
                    val intent = ChannelActivity.newIntentFromCustomActivity(
                        requireContext(),
                        CustomChatActivity::class.java,
                        channel.url
                    )
                    startActivity(intent)
                }
            }
        }
    }

    override fun onBindHeaderComponent(
        headerComponent: HeaderComponent,
        viewModel: ChannelListViewModel,
    ) {
        super.onBindHeaderComponent(headerComponent, viewModel)

        if (headerComponent is CustomHeaderComponent) {
            val header = module.headerComponent as CustomHeaderComponent

            header.search = View.OnClickListener {
                val intent = Intent(requireContext(), MessageSearchActivity::class.java)
                startActivity(intent)
            }
        }
    }
}