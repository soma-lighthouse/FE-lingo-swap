package com.lighthouse.auth.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.auth.databinding.LanguageLevelTileBinding
import com.lighthouse.domain.entity.response.vo.LanguageVO

class LanguageLevelAdapter(
    private val context: Context,
    private val dataList: MutableList<LanguageVO>,
    private val countrySelect: (Int) -> Unit,
    private val levelSelect: (Int, Int) -> Unit,
) : RecyclerView.Adapter<LanguageLevelAdapter.ViewHolder>() {
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
            binding.tvLanguage.text = language.name

            binding.tvLanguage.setOnClickListener {
                countrySelect(position)
            }

            binding.btnDel.apply {
                if (position == 0) {
                    setGone()
                }
                setOnClickListener {
                    Log.d("TESTING", position.toString())
                    dataList.removeAt(position)
                    notifyItemRemoved(position)

                    for (i in position until dataList.size) {
                        notifyItemChanged(i)
                    }
                }
            }
            binding.spinnerLevel.adapter = initSpinner()
            binding.spinnerLevel.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        p: Int,
                        id: Long,
                    ) {
                        levelSelect(p, position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
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