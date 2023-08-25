package com.lighthouse.android.common_ui.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lighthouse.android.common_ui.R

class SimpleListAdapter<T : Any, B : ViewDataBinding>(
    val diffCallBack: DiffUtil.ItemCallback<T>,
    private val layoutId: Int,
    private val onBindCallback: (ViewHolder<B>, T) -> Unit,
) : ListAdapter<T, ViewHolder<B>>(diffCallBack) {

    private var isLoadingVisible = false

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> {
        val inflater = LayoutInflater.from(parent.context)

        val binding = when (viewType) {
            TYPE_ITEM -> DataBindingUtil.inflate<B>(inflater, layoutId, parent, false)
            TYPE_LOADING -> DataBindingUtil.inflate<B>(
                inflater,
                R.layout.progress_item,
                parent,
                false
            )

            else ->
                throw IllegalArgumentException("Invalid viewType")
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
        if (getItemViewType(position) == TYPE_ITEM) {
            onBindCallback(holder, getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingVisible && position == itemCount - 1) {
            TYPE_LOADING
        } else {
            TYPE_ITEM
        }
    }

    override fun submitList(list: List<T>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

}