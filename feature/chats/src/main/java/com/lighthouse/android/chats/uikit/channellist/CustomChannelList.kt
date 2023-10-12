package com.lighthouse.android.chats.uikit.channellist

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import com.lighthouse.android.chats.view.CustomChatActivity
import com.lighthouse.android.common_ui.util.Injector
import com.lighthouse.navigation.MainNavigator
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.exception.SendbirdException
import com.sendbird.uikit.activities.ChannelActivity
import com.sendbird.uikit.fragments.ChannelListFragment
import com.sendbird.uikit.model.ReadyStatus
import com.sendbird.uikit.modules.ChannelListModule
import com.sendbird.uikit.vm.ChannelListViewModel
import dagger.hilt.android.EntryPointAccessors


open class CustomChannelList : ChannelListFragment() {
    private val mainNavigator: MainNavigator by lazy {
        EntryPointAccessors.fromActivity(
            requireActivity(),
            Injector.MainNavigatorInjector::class.java
        ).mainNavigator()
    }

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
        module.channelListComponent.setAdapter(CustomChannelListAdapter {
            mainNavigator.navigateToProfile(
                requireContext(),
                Pair("userId", it),
                Pair("isMe", false),
                Pair("isChat", true)
            )
        })
        module.channelListComponent.setOnItemClickListener { _, _, channel ->
            GroupChannel.getChannel(channel.url) { _, e ->
                if (e != null) {
                    // TODO("error handling")
                } else {
                    joinChannel(channel.url)
                }
            }
        }
        module.channelListComponent.setOnItemLongClickListener { _, _, data ->
            showListContextMenu(data)
        }
    }


    private fun showListContextMenu(channel: GroupChannel) {
        if (context == null) return
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val leaveItem: CharSequence =
            getString(com.lighthouse.android.common_ui.R.string.leave_channel)
        val isOff = channel.myPushTriggerOption == GroupChannel.PushTriggerOption.OFF
        val notificationItem: CharSequence =
            if (isOff) getString(com.lighthouse.android.common_ui.R.string.turn_on_alarm) else getString(
                com.lighthouse.android.common_ui.R.string.turn_off_alarm
            )
        val items = arrayOf(leaveItem, notificationItem)
        builder.setItems(items) { dialog, which ->
            dialog.dismiss()
            if (which == 0) {
                viewModel.leaveChannel(channel) { e: SendbirdException? ->
                    if (e == null) return@leaveChannel
                    Toast.makeText(
                        requireContext(),
                        com.lighthouse.android.common_ui.R.string.leave_channel,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                leaveChannel(channel)
            } else {
                viewModel.setPushNotification(
                    channel, isOff
                ) { e: SendbirdException? ->
                    if (e == null) return@setPushNotification
                    val errorString: Int =
                        if (isOff) com.lighthouse.android.common_ui.R.string.turn_off_alarm else com.lighthouse.android.common_ui.R.string.turn_on_alarm
                    Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.show()
    }


    protected fun joinChannel(channelUrl: String) {
        val intent = ChannelActivity.newIntentFromCustomActivity(
            requireContext(),
            CustomChatActivity::class.java,
            channelUrl
        )
        startActivity(intent)
    }


}