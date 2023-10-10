package com.lighthouse.board.adapter

import android.content.Context
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
    context: Context,
    likeListener: (questionId: Int) -> Unit,
    cancelListener: (questionId: Int) -> Unit,
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

            binding.ivLike.isChecked = item.clicked

            if (item.clicked) {
                binding.root.context.getColor(R.color.main)
            } else {
                binding.root.context.getColor(R.color.brown_grey)
            }

            binding.ivLike.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val num = binding.tvLike.text.toString().toInt()
                    binding.tvLike.text = num.plus(1).toString()
                    likeListener(item.questionId)
                } else {
                    val num = binding.tvLike.text.toString().toInt()
                    binding.tvLike.text = num.minus(1).toString()
                    cancelListener(item.questionId)
                }
            }


            binding.ivProfile.setOnClickListener {
                navigateToProfile(item.uuid)
            }

            binding.setVariable(BR.item, item)

        },
        context = context,
        ads = true
    )
