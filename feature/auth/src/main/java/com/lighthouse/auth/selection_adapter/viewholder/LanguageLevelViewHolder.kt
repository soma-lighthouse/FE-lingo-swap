package com.lighthouse.auth.selection_adapter.viewholder

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.util.setGone
import com.lighthouse.auth.databinding.LanguageLevelTileBinding
import com.lighthouse.auth.viewmodel.AuthViewModel

class LanguageLevelViewHolder(
    private val binding: LanguageLevelTileBinding,
    private val context: Context,
) :
    RecyclerView.ViewHolder(binding.root) {

    private val arrayList = arrayListOf(
        context.resources.getString(R.string.level1),
        context.resources.getString(R.string.level2),
        context.resources.getString(R.string.level3),
        context.resources.getString(R.string.level4),
        context.resources.getString(R.string.level5)
    )

    fun onBind(position: Int, listener: AuthViewModel) {
        val language = listener.language[position]

        binding.tvLanguage.text = language.name
        val position = absoluteAdapterPosition
//        binding.tvLanguage.setOnClickListener {
//            clickListener?.countrySelect(position)
//        }

        binding.btnDel.apply {
            Log.d("TESTING", position.toString())
            if (position == 0) {
                setGone()
            }
//            setOnClickListener {
//                clickListener?.deleteSelect(position)
//            }
        }
        binding.spinnerLevel.adapter = initSpinner()
        binding.spinnerLevel.setSelection(language.level - 1)
        binding.spinnerLevel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    p: Int,
                    id: Long,
                ) {
//                    clickListener?.levelSelect(p, position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
    }

    private fun initSpinner() = ArrayAdapter(
        context,
        R.layout.spinner_item,
        arrayList
    ).apply {
        setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
    }
}