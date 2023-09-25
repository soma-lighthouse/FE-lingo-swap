package com.lighthouse.android.chats.adapter

import com.lighthouse.android.chats.R
import com.lighthouse.android.chats.databinding.ChatQuestionTileBinding
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter

fun makeAdapter(
    sendMessage: (String) -> Unit,
) = SimpleListAdapter<String, ChatQuestionTileBinding>(
    diffCallBack = ItemDiffCallBack(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old == new }
    ),
    layoutId = R.layout.chat_question_tile,
    onBindCallback = { viewHolder, item ->
        val binding = viewHolder.binding
        binding.tvQuestion.text = item
        binding.root.setOnClickListener {
            sendMessage(item)
        }
    }
)