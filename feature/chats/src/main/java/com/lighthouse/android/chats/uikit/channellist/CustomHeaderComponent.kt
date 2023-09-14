package com.lighthouse.android.chats.uikit.channellist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lighthouse.android.chats.databinding.CustomHeaderBinding
import com.sendbird.uikit.modules.components.HeaderComponent

class CustomHeaderComponent : HeaderComponent() {
    private lateinit var binding: CustomHeaderBinding
    var search: View.OnClickListener? = null

    override fun onCreateView(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        args: Bundle?,
    ): View {
        binding = CustomHeaderBinding.inflate(inflater, null, false)
        return binding.root
    }
}