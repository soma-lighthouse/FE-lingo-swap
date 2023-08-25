package com.lighthouse.board.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.lighthouse.android.common_ui.BR
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.QuestionTileBinding
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.domain.entity.response.vo.BoardQuestionVO

fun makeAdapter(
    likeListener: (questionId: Int, userId: String) -> Unit,
    cancelListener: (questionId: Int, userId: String) -> Unit,
    navigateToProfile: (userId: String) -> Unit,
) =
    SimpleListAdapter<BoardQuestionVO, QuestionTileBinding>(
        diffCallBack = ItemDiffCallBack(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old.questionId == new.questionId }
        ),
        layoutId = R.layout.question_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding

            Glide.with(binding.ivProfile).load(item.profileImageUri)
                .placeholder(R.drawable.placeholder)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerInside()
                .override(calSize(Constant.PROFILE_IMAGE_SIZE))
                .dontAnimate()
                .into(binding.ivProfile)

            val flag = binding.root.context.resources.getIdentifier(
                item.region, "drawable", binding.root.context.packageName
            )

            binding.tvContent.text = item.contents
            binding.tvLike.text = item.like.toString()
            binding.tvName.text = item.name

            binding.ivFlag.setImageResource(flag)
            binding.ivFlag.layoutParams.width = calSize(16f)
            binding.ivFlag.layoutParams.height = calSize(16f)
            binding.ivFlag.requestLayout()

            binding.ivLike.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val num = binding.tvLike.text.toString().toInt()
                    binding.tvLike.text = num.plus(1).toString()
                    likeListener(item.questionId, item.userId)
                } else {
                    val num = binding.tvLike.text.toString().toInt()
                    binding.tvLike.text = num.minus(1).toString()
                    cancelListener(item.questionId, item.userId)
                }
            }

            binding.ivProfile.setOnClickListener {
                navigateToProfile(item.userId)
            }

            binding.setVariable(BR.item, item)

        }
    )
