package com.lighthouse.board.adapter

import android.content.Context
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.QuestionTileBinding
import com.lighthouse.board.viewmodel.BoardViewModel
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO

fun makeAdapter(
    context: Context,
    viewModel: BoardViewModel,
    navigateToProfile: (userId: String) -> Unit
) =
    SimpleListAdapter<BoardQuestionVO, QuestionTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.questionId == new.questionId }
        ),
        layoutId = R.layout.question_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding
            val position = viewHolder.absoluteAdapterPosition
            binding.listener = viewModel
            binding.item = viewModel.getQuestion()?.get(position)
            if (item.clicked) {
                binding.root.context.getColor(R.color.main)
            } else {
                binding.root.context.getColor(R.color.brown_grey)
            }

            binding.ivProfile.setOnClickListener {
                navigateToProfile(item.uuid)
            }
        },
        context = context,
        ads = true
    )
