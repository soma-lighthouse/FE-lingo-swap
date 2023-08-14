package com.lighthouse.auth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.auth.databinding.LanguageTileBinding
import com.lighthouse.domain.entity.response.vo.LanguageVO

class LanguageAdapter(
    private val clickResult: (String) -> Unit,
) : ListAdapter<LanguageVO, LanguageAdapter.ViewHolder>(
    ItemDiffCallBack(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old.name == new.name }
    )
) {
    inner class ViewHolder(
        private val binding: LanguageTileBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: LanguageVO) {
            binding.tvCountry.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LanguageTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.onBind(currentItem)
        holder.itemView.setOnClickListener {
            clickResult(currentItem.name)
        }
    }
}