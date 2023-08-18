package com.lighthouse.auth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.util.Constant
import com.lighthouse.android.common_ui.util.calSize
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.android.common_ui.util.setVisible
import com.lighthouse.auth.databinding.CountryTileBinding
import com.lighthouse.domain.entity.response.vo.CountryVO

class CountryAdapter(
    private val listener: OnItemClickListener,
    private val multiSelection: Boolean,
) : ListAdapter<CountryVO, CountryAdapter.ViewHolder>(
    ItemDiffCallBack(
        onItemsTheSame = { old, new -> old.select == new.select },
        onContentsTheSame = { old, new -> old == new }
    )
) {

    inner class ViewHolder(private val binding: CountryTileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: CountryVO) {
            binding.tvCountry.text = item.name

            val flag = binding.root.context.resources.getIdentifier(
                item.code, "drawable", binding.root.context.packageName
            )
            binding.ivFlag.setImageResource(flag)
            binding.ivFlag.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
            binding.ivFlag.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
            binding.ivFlag.requestLayout()

            if (item.select) {
                binding.btnCheck.setVisible()
            } else {
                binding.btnCheck.setGone()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CountryTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.onBind(currentItem)
        holder.itemView.setOnClickListener {
            if (multiSelection) {
                currentItem.select = !currentItem.select
                listener.onItemClick(currentItem)
                notifyItemChanged(position)
            } else {
                if (currentItem.select) {
                    clearSelection()
                    currentItem.select = false
                } else {
                    clearSelection()
                    currentItem.select = true
                }
                listener.onItemClick(currentItem)
                notifyItemChanged(position)
            }
        }
    }

    private fun clearSelection() {
        for (i in currentList.indices) {
            if (currentList[i].select) {
                currentList[i].select = false
                notifyItemChanged(i)
            }
        }
    }

    fun interface OnItemClickListener {
        fun onItemClick(item: CountryVO)
    }
}