package com.lighthouse.android.common_ui.base.selection_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.databinding.CountryTileBinding
import com.lighthouse.android.common_ui.databinding.LanguageLevelTileBinding
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.LanguageVO
import com.lighthouse.domain.entity.response.vo.Selection

class SelectionAdapter(
    private val multiSelection: Boolean,
    private val listener: OnItemClickListener? = null,
    private val langListener: OnItemClickListenerLang? = null,
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

    var selectCnt = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (type) {
            COUNTRY -> CountryViewHolder(CountryTileBinding.inflate(inflater, parent, false))
            LEVEL -> LanguageLevelViewHolder(
                LanguageLevelTileBinding.inflate(
                    inflater,
                    parent,
                    false
                ), langListener, context
            )

            else -> throw IllegalArgumentException("invalid item Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is CountryViewHolder -> holder.onBind(item as CountryVO)
            is LanguageLevelViewHolder -> holder.onBind(item as LanguageVO)
        }
        if (holder !is LanguageLevelViewHolder) {
            holder.itemView.setOnClickListener {
                if (multiSelection && selectCnt < Constant.MAX_SELECTION) {
                    item.select = !item.select
                    if (item.select) selectCnt += 1 else selectCnt -= 1
                    listener?.onItemClick(item)
                    notifyItemChanged(position)
                } else if (!multiSelection) {
                    clearSelection()
                    item.select = !item.select
                    listener?.onItemClick(item)
                    notifyItemChanged(position)
                } else if (item.select && selectCnt == Constant.MAX_SELECTION) {
                    item.select = false
                    selectCnt -= 1
                    listener?.onItemClick(item)
                    notifyItemChanged(position)
                }
            }
        }


    }


    fun interface OnItemClickListener {
        fun onItemClick(item: Selection)
    }

    interface OnItemClickListenerLang {
        fun countrySelect(position: Int)
        fun levelSelect(level: Int, position: Int)
        fun deleteSelect(position: Int)
    }


    private fun clearSelection() {
        selectCnt = 0
        for (i in currentList.indices) {
            if (currentList[i].select) {
                currentList[i].select = false
                notifyItemChanged(i)
            }
        }
    }
}