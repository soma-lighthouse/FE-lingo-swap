package com.lighthouse.auth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.auth.databinding.LanguageLevelTileBinding
import com.lighthouse.domain.entity.response.vo.LanguageVO

class LanguageAdapter(
    private val context: Context,
    private val dataList: MutableList<LanguageVO>,
    private val countrySelect: () -> Unit,
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {
    private val arrayList = arrayListOf(
        context.resources.getString(R.string.level1),
        context.resources.getString(R.string.level2),
        context.resources.getString(R.string.level3),
        context.resources.getString(R.string.level4),
        context.resources.getString(R.string.level5)
    )

    inner class ViewHolder(private val binding: LanguageLevelTileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(language: LanguageVO, position: Int) {
            binding.tvLanguage.text = language.code

            binding.tvLanguage.setOnClickListener {
                countrySelect()
            }

            binding.btnDel.apply {
                if (position == 0) {
                    setGone()
                }
                setOnClickListener {
                    dataList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
            binding.spinnerLevel.adapter = initSpinner()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LanguageLevelTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(dataList[position], position)
    }

    override fun getItemCount() = dataList.size

    private fun initSpinner() = ArrayAdapter(
        context,
        R.layout.spinner_item,
        arrayList
    ).apply {
        setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)

    }

}