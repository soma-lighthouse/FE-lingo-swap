package com.lighthouse.profile

import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.lighthouse.profile.viewmodel.ProfileViewModel

@BindingAdapter("setUpMenu")
fun setUpMenu(toolBar: MaterialToolbar, viewModel: ProfileViewModel) {
    toolBar.setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.item_edit -> {
                viewModel.isEdit.set(true)
                viewModel.startTime = System.currentTimeMillis().toDouble()
                true
            }

            else -> {
                true
            }
        }
    }

    if (viewModel.isEdit.get() || !viewModel.isMe.get()) {
        toolBar.menu.clear()
    } else if (toolBar.menu.size() == 0) {
        toolBar.menu.add(
            0,
            R.id.item_edit,
            0,
            toolBar.context.getString(com.lighthouse.android.common_ui.R.string.edit)
        )
    }
}