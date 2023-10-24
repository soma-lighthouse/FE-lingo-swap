package com.lighthouse.android.chats.adapter

import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.chats.R

@BindingAdapter("currentChipPosition")
fun currentChipPosition(chip: ChipGroup, position: MutableLiveData<Int>) {
    if (chip.getTag(R.id.chipListener) == null) {
        chip.setOnCheckedStateChangeListener { _, checkedIds ->
            position.value = checkedIds.first()
        }
    }
}