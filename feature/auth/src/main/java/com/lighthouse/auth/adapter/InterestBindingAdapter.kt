package com.lighthouse.auth.adapter

import android.view.LayoutInflater
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.domain.entity.response.vo.InterestVO

@BindingAdapter("updateInterestChip")
fun updateInterestChip(chipGroup: ChipGroup, interests: MutableLiveData<List<InterestVO>>) {
    chipGroup.removeAllViews()
    val inflater = LayoutInflater.from(chipGroup.context)
    interests.value?.flatMap { it.interests }?.forEach {
        val chip = inflater.inflate(
            com.lighthouse.android.common_ui.R.layout.home_chip, chipGroup, false
        ) as Chip
        chip.text = it.name
        chip.isCloseIconVisible = false
        chipGroup.addView(chip)
    }
}