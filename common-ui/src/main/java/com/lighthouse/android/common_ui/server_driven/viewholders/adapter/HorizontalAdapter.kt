package com.lighthouse.android.common_ui.server_driven.viewholders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.databinding.LanguageTabBinding
import com.lighthouse.android.common_ui.server_driven.rich_text.SpannableStringBuilderProvider
import com.lighthouse.android.common_ui.server_driven.viewholders.util.ItemDiffCallback
import com.lighthouse.domain.entity.response.server_driven.RichText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HorizontalAdapter : ListAdapter<List<RichText>, HorizontalAdapter.HorizontalViewHolder>(
    ItemDiffCallback<List<RichText>>(
        onContentsTheSame = { old, new -> old == new },
        onItemsTheSame = { old, new -> old == new }
    )
) {
    inner class HorizontalViewHolder(private val binding: LanguageTabBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(text: List<RichText>) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.tvLanguage.text =
                    SpannableStringBuilderProvider.getSpannableBuilder(text, binding.root.context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val binding = LanguageTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}