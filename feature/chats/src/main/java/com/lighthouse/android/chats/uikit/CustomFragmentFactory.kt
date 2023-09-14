package com.lighthouse.android.chats.uikit

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lighthouse.android.chats.uikit.channel.CustomChannel
import com.lighthouse.android.chats.view.CustomSearchFragment
import com.sendbird.uikit.fragments.ChannelFragment
import com.sendbird.uikit.fragments.MessageSearchFragment
import com.sendbird.uikit.fragments.UIKitFragmentFactory

class CustomFragmentFactory : UIKitFragmentFactory() {
    override fun newChannelFragment(channelUrl: String, args: Bundle): Fragment {
        return ChannelFragment.Builder(channelUrl)
            .setCustomFragment(CustomChannel())
            .withArguments(args)
            .build()
    }

    override fun newMessageSearchFragment(channelUrl: String, args: Bundle): Fragment {
        return MessageSearchFragment.Builder(channelUrl)
            .setCustomFragment(CustomSearchFragment())
            .withArguments(args)
            .build()
    }
}