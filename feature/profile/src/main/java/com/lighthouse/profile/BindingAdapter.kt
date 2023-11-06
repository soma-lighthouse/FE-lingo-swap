package com.lighthouse.profile

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import com.google.android.material.appbar.MaterialToolbar

@BindingAdapter("setUpMenu")
fun setUpMenu(toolBar: MaterialToolbar, editMode: ObservableBoolean) {
    toolBar.setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.item_edit -> {
                editMode.set(true)
                true
            }

            else -> {
                true
            }
        }
    }

    if (editMode.get()) {
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