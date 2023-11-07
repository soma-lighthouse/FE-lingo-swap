package com.lighthouse.android.common_ui.server_driven.viewholders.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


class InflateViewType {
    companion object {
        fun<T: ViewDataBinding> inflateView(parent: ViewGroup, layout: Int): T =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layout,
                parent,
                false
            )
    }
}