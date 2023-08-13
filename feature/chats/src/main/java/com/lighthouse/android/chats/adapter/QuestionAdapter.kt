package com.lighthouse.android.chats.adapter

import com.lighthouse.android.chats.R
import com.lighthouse.android.chats.databinding.ChatQuestionTileBinding
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.domain.response.vo.BoardQuestionVO

fun makeAdapter(
    sendMessage: (String) -> Unit,
) = SimpleListAdapter<BoardQuestionVO, ChatQuestionTileBinding>(
    diffCallBack = ItemDiffCallBack(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old.questionId == new.questionId }
    ),
    layoutId = R.layout.chat_question_tile,
    onBindCallback = { viewHolder, item ->
        val binding = viewHolder.binding
        binding.tvQuestion.text = item.contents
        binding.root.setOnClickListener {
            sendMessage(item.contents)
        }
    }
)