package com.lighthouse.android.common_ui.base.adapter

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.chip.Chip
import com.lighthouse.android.common_ui.databinding.InterestListTileBinding
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.domain.entity.response.vo.InterestVO

fun makeAdapter(
    checkable: Boolean = false,
    hide: Boolean = false,
    selectedList: HashMap<String, List<String>> = hashMapOf(),
    chip: ((List<Int>, Int) -> Unit)? = null,
) =
    SimpleListAdapter<InterestVO, InterestListTileBinding>(diffCallBack = ItemDiffCallBack(
        onItemsTheSame = { old, new -> old.interest == new.interest },
        onContentsTheSame = { old, new -> old == new }),
        layoutId = com.lighthouse.android.common_ui.R.layout.interest_list_tile,
        onBindCallback = { viewHolder, item ->
            val binding = viewHolder.binding

            binding.tvInterestTitle.text = item.category

            if (viewHolder.adapterPosition != 0 && hide) {
                binding.chipInterest.setGone()
                binding.bottomLine.setGone()
                binding.btnInterest.rotation = 180f
            } else {
                binding.btnInterest.rotation = 0f
            }

            binding.clickRectangle.setOnClickListener {
                if (binding.chipInterest.visibility == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(
                        binding.collapseInterest, AutoTransition()
                    )
                    binding.chipInterest.setGone()
                    binding.bottomLine.setGone()
                    binding.btnInterest.animate().rotation(180f).start()
                } else {
                    TransitionManager.beginDelayedTransition(
                        binding.collapseInterest, AutoTransition()
                    )
                    binding.chipInterest.setVisible()
                    binding.bottomLine.setVisible()
                    binding.btnInterest.animate().rotation(0f).start()
                }
            }

            val inflater = LayoutInflater.from(binding.root.context)

            val check = selectedList[item.category]

            item.interest.forEach {
                val chip = inflater.inflate(
                    com.lighthouse.android.common_ui.R.layout.chip, binding.chipInterest, false
                ) as Chip
                chip.text = it
                chip.isCloseIconVisible = false
                chip.isCheckable = checkable
                if (check != null && it in check) {
                    chip.isChecked = true
                    
                }

                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked && binding.chipInterest.checkedChipIds.size > Constant.MAX_SELECTION) {
                        chip.isChecked = false

                    }
                }

                binding.chipInterest.addView(chip)
            }

            binding.chipInterest.setOnCheckedStateChangeListener { _, checkedId ->
                if (chip != null) {
                    chip(checkedId, viewHolder.adapterPosition)
                }
            }
        })