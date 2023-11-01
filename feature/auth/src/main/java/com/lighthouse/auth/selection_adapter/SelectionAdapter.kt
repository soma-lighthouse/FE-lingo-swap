package com.lighthouse.auth.selection_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.auth.databinding.CountryTileBinding
import com.lighthouse.auth.databinding.LanguageLevelTileBinding
import com.lighthouse.auth.selection_adapter.viewholder.CountryViewHolder
import com.lighthouse.auth.selection_adapter.viewholder.LanguageLevelViewHolder
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.response.vo.Selection

class SelectionAdapter(
    private val multiSelection: Boolean,
    private val viewModel: AuthViewModel,
    private val type: Int,
    private val context: Context,
) : ListAdapter<Selection, RecyclerView.ViewHolder>(
    ItemDiffCallBack<Selection>(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old.select == new.select }
    )
) {
    companion object {
        const val COUNTRY = 2
        const val LEVEL = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (type) {
            COUNTRY -> CountryViewHolder(CountryTileBinding.inflate(inflater, parent, false))
            LEVEL -> LanguageLevelViewHolder(
                LanguageLevelTileBinding.inflate(
                    inflater,
                    parent,
                    false
                ), context
            )

            else -> throw IllegalArgumentException("invalid item Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CountryViewHolder -> holder.onBind(position, viewModel, multiSelection)
            is LanguageLevelViewHolder -> holder.onBind(position, viewModel)
        }
    }
}