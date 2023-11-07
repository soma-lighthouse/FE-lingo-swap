package com.lighthouse.android.home.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch


@BindingAdapter("spannableText")
fun setSpannableText(view: TextView, viewModel: HomeViewModel) {
    viewModel.viewModelScope.launch {
        val spannableStringBuilder = viewModel.getSpannableText()
        view.text = spannableStringBuilder
    }
}
