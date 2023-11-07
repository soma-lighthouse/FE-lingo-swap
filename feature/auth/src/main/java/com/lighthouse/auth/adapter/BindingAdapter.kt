package com.lighthouse.auth.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.util.observe
import com.lighthouse.auth.viewmodel.AuthViewModel
import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.CountryVO
import com.lighthouse.domain.entity.response.vo.InterestVO
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

@BindingAdapter(value = ["parseBirthday", "setUpObserver"], requireAll = true)
fun setParsedBirthday(editText: EditText, register: RegisterInfoVO, observable: ObservableBoolean) {
    observable.observe {
        register.birthday = convertToStandardDateFormat(editText.text.toString())
    }
}

@BindingAdapter(value = ["setUpChip", "closeListener"], requireAll = false)
fun setUpSelectChip(
    chipGroup: ChipGroup,
    selected: MutableLiveData<List<CountryVO>>?,
    closeListener: AuthViewModel?
) {
    if (selected == null) return

    chipGroup.removeAllViews()
    val context = chipGroup.context
    val inflater = LayoutInflater.from(context)
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

private fun convertToStandardDateFormat(inputDate: String): String {
    val formats = arrayOf(
        "ddMMyyyy",
        "dd/MM/yyyy",
        "dd-MM-yyyy"
    )
    var parsedDate: LocalDate? = null

    for (format in formats) {
        try {
            val sdf = DateTimeFormatter.ofPattern(format)
            parsedDate = LocalDate.parse(inputDate, sdf)
            if (parsedDate != null) {
                break // Successfully parsed the date
            }
        } catch (e: Exception) {
            // Parsing with this format failed, try the next format
        }
    }

    Log.d("TESTING BIRTHDAY", parsedDate.toString())

    return parsedDate.toString()
}

@BindingAdapter("updateInterestChip")
fun updateInterestChip(chipGroup: ChipGroup, interests: MutableLiveData<List<InterestVO>>) {
    chipGroup.removeAllViews()
    val inflater = LayoutInflater.from(chipGroup.context)
    interests.value?.flatMap { it.interests }?.forEach {
        val chip = inflater.inflate(
            com.lighthouse.android.common_ui.R.layout.home_chip, chipGroup, false
        ) as Chip
        chip.text = it.name
        chip.isCloseIconVisible = false
        chipGroup.addView(chip)
    }
}

@BindingAdapter(value = ["setUpSpinner", "level"], requireAll = true)
fun setUpSpinner(spinner: AppCompatSpinner, list: List<String>, level: Int) {
    val context = spinner.context
    spinner.adapter = ArrayAdapter(
        context,
        R.layout.spinner_item,
        list
    ).apply {
        setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
    }

    spinner.setSelection(level - 1)
}

@BindingAdapter(value = ["onLanguageLevelSelected", "viewModel"], requireAll = true)
fun onLanguageLevelSelected(spinner: AppCompatSpinner, position: Int, viewModel: AuthViewModel) {
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            p: Int,
            id: Long,
        ) {
            viewModel.selectedLanguage.value?.get(position)?.level = p + 1
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Do nothing
        }
    }
}