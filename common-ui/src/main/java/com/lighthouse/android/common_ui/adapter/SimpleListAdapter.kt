package com.lighthouse.android.common_ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class SimpleListAdapter<T : Any, B : ViewDataBinding>(
    diffCallBack: DiffUtil.ItemCallback<T>,
    @LayoutRes private val layoutId: Int,
    private val onBindCallback: (ViewHolder<B>, T) -> Unit,
) : ListAdapter<T, ViewHolder<B>>(diffCallBack) {
    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> {
        val inflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<B>(inflater, layoutId, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
        onBindCallback(holder, getItem(position))
    }
}