package com.lighthouse.auth.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.util.observe
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.request.UploadInterestVO
import com.lighthouse.domain.entity.response.vo.CountryVO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@BindingAdapter(value = ["isValid", "number", "onError"], requireAll = true)
fun isValid(view: View, tagNumber: Int, number: MutableLiveData<List<Int>>, errorMessage: String) {
    if (number.value?.contains(tagNumber) == true) {
        setErrorAndBackground(view, false, errorMessage)
    } else {
        setErrorAndBackground(view, true, errorMessage)
    }
}

@BindingAdapter(value = ["getSpinnerValue", "setUpObserver"], requireAll = true)
fun getSpinnerValue(
    spinner: AppCompatSpinner,
    register: RegisterInfoVO,
    observable: ObservableBoolean
) {
    observable.observe {
        val position = spinner.selectedItemPosition
        val genderMap = mapOf(
            0 to "TMP",
            1 to "MALE",
            2 to "FEMALE",
            3 to "RATHER_NOT_SAY"
        )
        Log.d("TESTING REGISTER", position.toString())
        register.gender = genderMap[position]
    }
}

@BindingAdapter(value = ["getAdapterValue", "setUpObserver"], requireAll = true)
fun getAdapterValue(
    adapter: RecyclerView,
    register: RegisterInfoVO,
    observable: ObservableBoolean
) {
    observable.observe {
        val adapterValue = adapter.adapter
        if (adapterValue is ListAdapter<*, *>) {
            register.preferredInterests = adapterValue.currentList as List<UploadInterestVO>
        }
    }
}

@BindingAdapter(value = ["parseBirthday", "setUpObserver"], requireAll = true)
fun setParsedBirthday(editText: EditText, register: RegisterInfoVO, observable: ObservableBoolean) {
    observable.observe {
        register.birthday = convertToStandardDateFormat(editText.text.toString())
    }
}

@BindingAdapter(value = ["setUpChip", "closeListener"], requireAll = false)
fun setUpSelectChip(
    chipGroup: ChipGroup,
    selected: MutableLiveData<List<CountryVO>>,
    closeListener: AuthViewModel?
) {
    chipGroup.removeAllViews()
    val context = chipGroup.context
    val inflater = LayoutInflater.from(context)
    Log.d("TESTING", "setUpSelectChip: ${selected.value}")
    selected.value?.forEach {
        if (it.name.isEmpty()) return@forEach
        val chip = inflater.inflate(R.layout.home_chip, null, false) as Chip
        chip.text = it.name
        chip.isCheckable = false
        chip.setOnCloseIconClickListener { _ ->
            closeListener?.chipCloseListener(it)
        }
        if (closeListener == null) {
            chip.isCloseIconVisible = false
        }
        chipGroup.addView(chip)
    }
}

private fun setErrorAndBackground(
    view: View,
    isValid: Boolean,
    errorMessage: String,
) {
    if (view is EditText) {
        if (!isValid) {
            view.error = errorMessage
            view.setBackgroundResource(R.drawable.error_box)
            view.requestFocus()
        } else {
            view.error = null
            view.setBackgroundResource(R.drawable.edit_box)
        }
    } else {
        if (!isValid) {
            view.setBackgroundResource(R.drawable.error_box)
            view.requestFocus()
        } else {
            view.setBackgroundResource(R.drawable.edit_box)
        }
    }
}

private fun convertToStandardDateFormat(inputDate: String): String {
    val inputFormat = listOf(
        SimpleDateFormat("yyyy-M-d", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-d", Locale.getDefault()),
        SimpleDateFormat("yyyy-M-dd", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    )
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    inputFormat.forEach {
        try {
            val parsedDate: Date = it.parse(inputDate) as Date
            return outputFormat.format(parsedDate)
        } catch (e: Exception) {
            // Ignore and try the next format
        }
    }
    return ""
}