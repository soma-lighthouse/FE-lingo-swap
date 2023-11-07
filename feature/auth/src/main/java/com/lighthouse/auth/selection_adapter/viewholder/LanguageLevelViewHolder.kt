package com.lighthouse.auth.selection_adapter.viewholder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.auth.databinding.LanguageLevelTileBinding
import com.lighthouse.auth.viewmodel.AuthViewModel

class LanguageLevelViewHolder(
    private val binding: LanguageLevelTileBinding,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(position: Int, listener: AuthViewModel) {
        Log.d("TESTING LANGUAGE", "onBind: ${listener.selectedLanguage.value}")
        val language = listener.selectedLanguage.value?.elementAtOrNull(position)
        language?.let {
            binding.item = language
            binding.viewModel = listener
            binding.position = position
        }
    }
}