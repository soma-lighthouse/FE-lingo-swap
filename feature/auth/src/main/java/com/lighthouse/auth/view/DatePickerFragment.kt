package com.lighthouse.auth.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private val calender = Calendar.getInstance()
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calender[Calendar.YEAR] = year
        calender[Calendar.MONTH] = month
        calender[Calendar.DAY_OF_MONTH] = dayOfMonth

        val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(calender.time)

        val selectedDateBundle = Bundle()
        selectedDateBundle.putSerializable("Birthday", selectedDate)

        setFragmentResult("Birthday", selectedDateBundle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = calender[Calendar.YEAR] - 20
        val month = calender[Calendar.MONTH]
        val day = calender[Calendar.DAY_OF_MONTH]

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }
}